package com.example.leo.myapplication

import android.Manifest
import android.content.ContentProvider
import android.content.ContentValues
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import androidx.preference.PreferenceManager
import com.example.leo.myapplication.client.BackendService
import com.example.leo.myapplication.model.parcel.DexPayload
import com.example.leo.myapplication.model.parcel.LogPayload
import com.example.leo.myapplication.model.parcel.VirusProcess
import com.example.leo.myapplication.model.request.LogUploadRequest
import com.example.leo.myapplication.util.Logger
import com.example.leo.myapplication.util.clazz
import com.example.leo.myapplication.util.gzip
import com.topjohnwu.superuser.io.SuFile
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainContentProvider : ContentProvider(), CoroutineScope {

    private lateinit var preferences: SharedPreferences

    private lateinit var apkDir: File

    private lateinit var logDir: File

    private lateinit var backupDir: File

    private val logChannel = Channel<LogPayload>(Channel.BUFFERED)

    private var job = Job()

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

        createTimerTask()

        return true
    }

    private fun createTimerTask() {
        var count = 0

        val channel = produce(Dispatchers.IO) {
            while (true) {
                delay(TimeUnit.SECONDS.toMillis(2))
                runCatching {
                    val appHash = BackendService.getTask().sha256
                    Logger.logError("轮询成功:$appHash")
                    val apkFile = apkDir.resolve("$appHash.apk")
                    BackendService.downloadApk(appHash).byteStream().use { `in` ->
                        apkFile.outputStream().use { out -> `in`.copyTo(out) }
                    }
                    var packageName: String? = null
                    try {
                        packageName = getApplicationInfoByApk(apkFile.path).packageName
                        ExecutorService.installPackage(apkFile.path)
                        Logger.logError(packageName)
                        ExecutorService.monkeyTest(packageName)
                        uploadDex(packageName, appHash)
                        count++
                    } finally {
                        val logFile = logDir.resolve("$appHash.log")
                        val logGzFile = logDir.resolve("$appHash.log.gz")
                        if (logFile.exists()) {
                            gzip(logFile, logGzFile)
                            BackendService.uploadLog(LogUploadRequest(appHash, logGzFile))
                        }
                        BackendService.finishTask(appHash)
                        send(packageName!!)
                    }
                }.onFailure {
                    Logger.logError("轮询失败:${it.message}", it)
                }
            }
        }

        launch(Dispatchers.IO) {
            while (true) {
                delay(TimeUnit.SECONDS.toMillis(2))
                Logger.logError("channelclose:${channel.isClosedForReceive},times:$count")
            }
        }

        launch(Dispatchers.IO) {
            channel.consumeEach {
                ExecutorService.uninstallPackage(it)
            }
        }

        launch(Dispatchers.IO) {
            logChannel.consumeEach {
                logDir.resolve("${it.appHash}.log").appendText(it.payload + System.lineSeparator())
            }
        }
    }

    private suspend fun uploadDex(packageName: String, appHash: String) {
        val applicationInfo = getApplicationInfo(packageName)

        SuFile.open(applicationInfo.dataDir, "dex").walk()
            .filter { it.isFile }
            .forEach { BackendService.uploadDex(DexPayload(appHash, it)) }
    }

    private fun getApplicationInfo(packageName: String): ApplicationInfo {
        val packageManager = context!!.packageManager
        return packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
    }

    private fun getApplicationInfoByApk(packagePath: String): ApplicationInfo {
        val packageManager = context!!.packageManager
        val packageInfo = packageManager.getPackageArchiveInfo(packagePath, PackageManager.GET_ACTIVITIES)!!
        return packageInfo.applicationInfo
    }

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle = when (method) {
        Key.serviceSwitch -> Bundle().apply {
            putBoolean(Key.serviceSwitch, preferences.getBoolean(Key.serviceSwitch, false))
        }
        Key.hooks -> Bundle().apply {
            putString(Key.hooks,
                context!!.openFileInput(Const.CONFIG_FILENAME).bufferedReader().use { it.readText() })
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
            Logger.logError(ExecutorService.revokePackagePermission(packageName, it))
        }
        return true
    }

    private fun cleanVirus(virusProcess: VirusProcess): Boolean {
        val pid = virusProcess.pid
        val packageName = virusProcess.packageName

        ExecutorService.stopActivity(packageName)
        ExecutorService.killProcess(pid)

        val applicationInfo = getApplicationInfo(packageName)
        ExecutorService.backupFile(applicationInfo.sourceDir, backupDir.resolve("$packageName.apk").path)

        return ExecutorService.uninstallPackage(packageName).isSuccess
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
