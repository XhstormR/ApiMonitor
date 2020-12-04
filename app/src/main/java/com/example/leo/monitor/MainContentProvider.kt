package com.example.leo.monitor

import android.Manifest
import android.content.ComponentName
import android.content.ContentProvider
import android.content.ContentValues
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.UserManager
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.example.leo.monitor.client.BackendService
import com.example.leo.monitor.model.parcel.DexPayload
import com.example.leo.monitor.model.parcel.LogPayload
import com.example.leo.monitor.model.parcel.VirusProcess
import com.example.leo.monitor.model.request.LogUploadRequest
import com.example.leo.monitor.util.Logger
import com.example.leo.monitor.util.clazz
import com.example.leo.monitor.util.gzip
import com.example.leo.monitor.util.toId
import com.topjohnwu.superuser.io.SuFile
import java.io.File
import java.net.InetAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

// TODO: Use Service https://developer.android.google.cn/guide/components/bound-services?hl=zh-cn#Messenger
class MainContentProvider : ContentProvider(), CoroutineScope {

    private lateinit var preferences: SharedPreferences

    private lateinit var apkDir: File

    private lateinit var logDir: File

    private lateinit var backupDir: File

    private val logChannel = Channel<LogPayload>(Channel.BUFFERED)

    private val uninstallChannel = Channel<String>()

    private var job = Job()

    private var backendJob: Job? = null

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    companion object {
        val DISABLE_PERMISSION = listOf(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,

            Manifest.permission.CALL_PHONE,
            Manifest.permission.CALL_PRIVILEGED,

            Manifest.permission.RECEIVE_MMS,

            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS,

            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,

            Manifest.permission.REBOOT,
            Manifest.permission.RECEIVE_BOOT_COMPLETED,

            Manifest.permission.READ_PHONE_STATE
        )
    }

    override fun onCreate(): Boolean {
        val dataDir = File(context!!.applicationInfo.dataDir)
        apkDir = dataDir.resolve("apk").apply { mkdirs() }
        logDir = dataDir.resolve("log").apply { mkdirs() }
        backupDir = dataDir.resolve("backup").apply { mkdirs() }

        preferences = PreferenceManager.getDefaultSharedPreferences(context)

        return true
    }

    private fun startBackendTask() {
        if (backendJob?.isActive == true) return
        if (job.isCompleted) job = Job()
        var count = 0

        backendJob = launch(Dispatchers.IO) {
            while (true) {
                delay(TimeUnit.SECONDS.toMillis(2))
                try {
                    val appHash = BackendService.getTask().sha256
                    Logger.logError("轮询成功:$appHash")

                    val packageName = try {
                        val apkFile = downloadApk(appHash)
                        val applicationInfo = getApplicationInfoByApk(apkFile)
                        ExecutorService.installPackage(getWorkUserId(), apkFile)
                        applicationInfo.packageName
                    } catch (e: Exception) {
                        Logger.logError("安装失败:${e.message}", e)
                        count++
                        BackendService.finishTask(appHash)
                        continue
                    }

                    try {
                        ExecutorService.startActivity(getWorkUserId(), getLaunchComponent(packageName))
                        delay(TimeUnit.SECONDS.toMillis(2))
                        val topCheck = launch(Dispatchers.IO) {
                            while (true) {
                                delay(TimeUnit.SECONDS.toMillis(1))
                                if (!isForeground(packageName)) {
                                    ExecutorService.startActivity(getWorkUserId(), getLaunchComponent(packageName))
                                }
                            }
                        }
                        ExecutorService.monkeyTest()
                        topCheck.cancel()
                    } catch (e: Exception) {
                        Logger.logError("测试失败:${e.message}", e)
                    } finally {
                        runCatching {
                            count++
                            uploadLog(appHash)
                            uploadDex(appHash, packageName)
                            ExecutorService.stopActivity(getWorkUserId(), packageName)
                            BackendService.finishTask(appHash)
                            uninstallChannel.send(packageName)
                        }.onFailure {
                            uninstallChannel.send(packageName)
                        }
                    }
                } catch (e: Exception) {
                    Logger.logError("轮询失败:${e.message}", e)
                }
            }
        }

        launch(Dispatchers.IO) {
            while (true) {
                delay(TimeUnit.SECONDS.toMillis(2))
                Logger.logError("times:$count")
            }
        }

        launch(Dispatchers.IO) {
            uninstallChannel.receiveAsFlow().collect {
                ExecutorService.uninstallPackage(getWorkUserId(), it)
            }
        }

        launch(Dispatchers.IO) {
            logChannel.receiveAsFlow().collect {
                logDir.resolve("${it.appHash}.log").appendText(it.payload + System.lineSeparator())
            }
        }
    }

    private suspend fun downloadApk(appHash: String): File {
        val file = apkDir.resolve("$appHash.apk")
        BackendService.downloadApk(appHash).byteStream().buffered().use { `in` ->
            file.outputStream().buffered().use { out -> `in`.copyTo(out) }
        }
        return file
    }

    private suspend fun uploadLog(appHash: String) {
        val logFile = logDir.resolve("$appHash.log")
        val logGzFile = logDir.resolve("$appHash.log.gz")
        if (logFile.exists()) {
            gzip(logFile, logGzFile)
            BackendService.uploadLog(LogUploadRequest(appHash, logGzFile))
        }
    }

    private suspend fun uploadDex(appHash: String, packageName: String) {
        val applicationInfo = getApplicationInfo(packageName)

        SuFile.open(applicationInfo.dataDir, "dex").walk()
            .filter { it.isFile }
            .forEach { BackendService.uploadDex(DexPayload(appHash, it)) }
    }

    private fun getApplicationInfo(packageName: String): ApplicationInfo {
        val packageManager = context!!.packageManager
        return packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
    }

    private fun getApplicationInfoByApk(packagePath: File): ApplicationInfo {
        val packageManager = context!!.packageManager
        val packageInfo = packageManager.getPackageArchiveInfo(packagePath.path, PackageManager.GET_ACTIVITIES)!!
        return packageInfo.applicationInfo
    }

    private fun getLaunchComponent(packageName: String): String {
        val packageManager = context!!.packageManager
        return packageManager
            .getLaunchIntentForPackage(packageName)!!
            .component!!
            .flattenToShortString()
    }

    private fun isForeground(packageName: String): Boolean {
        val topComponent = ExecutorService.getTopComponent()
        val componentName = ComponentName.unflattenFromString(topComponent) ?: return false
        return componentName.packageName == packageName
    }

    private fun getWorkUserId(): Int {
        val userManager = context!!.getSystemService(clazz<UserManager>())
        return userManager.userProfiles
            .map { it.toId() }
            .single { it != 0 }
    }

    private fun getIp(): String {
        val wifiManager = context!!.getSystemService(clazz<WifiManager>())
        return ByteBuffer.allocate(4)
            .order(ByteOrder.nativeOrder())
            .putInt(wifiManager.connectionInfo.ipAddress)
            .array()
            .let { InetAddress.getByAddress(it).hostAddress }
    }

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle = when (method) {
        Key.xposedSwitch -> Bundle().apply {
            putBoolean(Key.xposedSwitch, preferences.getBoolean(Key.xposedSwitch, false))
        }
        Key.fridaSwitch -> Bundle().apply {
            Toast.makeText(context, getIp(), Toast.LENGTH_LONG).show()
            val enable = preferences.getBoolean(Key.fridaSwitch, false)
            if (enable) ExecutorService.startFrida()
            else ExecutorService.stopFrida()
        }
        Key.backendSwitch -> Bundle().apply {
            val enable = preferences.getBoolean(Key.backendSwitch, false)
            if (enable) startBackendTask()
            else job.cancel()
        }
        Key.hooks -> Bundle().apply {
            val config = context!!.openFileInput(Const.CONFIG_FILENAME).bufferedReader().use { it.readText() }
            putString(Key.hooks, config)
        }
        Key.cleanVirusPackage -> Bundle().apply {
            extras ?: return Bundle.EMPTY
            val virusProcess = extras
                .apply { classLoader = clazz<MainActivity>().classLoader }
                .run { getParcelable<VirusProcess>(Key.cleanVirusPackage) }!!
            putBoolean(Key.cleanVirusPackage, cleanVirus(virusProcess))
        }
        Key.revokePermission -> Bundle().apply {
            extras ?: return Bundle.EMPTY
            val packageName = extras.getString(Key.revokePermission)!!
            putBoolean(Key.revokePermission, revokePackagePermission(packageName))
        }
        Key.uploadLog -> Bundle().apply {
            extras ?: return Bundle.EMPTY
            val logPayload = extras
                .apply { classLoader = clazz<MainActivity>().classLoader }
                .run { getParcelable<LogPayload>(Key.uploadLog) }!!
            putBoolean(Key.uploadLog, logChannel.offer(logPayload))
        }
        else -> Bundle.EMPTY
    }

    private fun revokePackagePermission(packageName: String): Boolean {
        DISABLE_PERMISSION.forEach {
            Logger.logError(ExecutorService.revokePackagePermission(getWorkUserId(), packageName, it))
        }
        return true
    }

    private fun cleanVirus(virusProcess: VirusProcess): Boolean {
        val pid = virusProcess.pid
        val packageName = virusProcess.packageName

        ExecutorService.stopActivity(getWorkUserId(), packageName)
        ExecutorService.killProcess(pid)

        val applicationInfo = getApplicationInfo(packageName)
        ExecutorService.backupFile(applicationInfo.sourceDir, backupDir.resolve("$packageName.apk").path)

        return ExecutorService.uninstallPackage(getWorkUserId(), packageName).isSuccess
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        TODO("not implemented")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        TODO("not implemented")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("not implemented")
    }

    override fun getType(uri: Uri): String? {
        TODO("not implemented")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        TODO("not implemented")
    }
}
