package com.example.leo.monitor.xposed

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.example.leo.monitor.Const
import com.example.leo.monitor.Key
import com.example.leo.monitor.model.parcel.LogPayload
import com.example.leo.monitor.model.parcel.VirusProcess
import com.example.leo.monitor.util.currentSystemContext

object BackgroundService {

    private val URI = Uri.Builder()
        .scheme(ContentResolver.SCHEME_CONTENT)
        .authority(Const.AUTHORITY)
        .build()

    fun getConfig() = doAction {
        val result = currentSystemContext()
            .contentResolver.call(URI, Key.hooks, null, null)

        result?.getString(Key.hooks) ?: ""
    }

    fun cleanVirusPackage(virusProcess: VirusProcess) = doAction {
        val extras = Bundle()
            .apply { putParcelable(Key.cleanVirusPackage, virusProcess) }
        val result = currentSystemContext()
            .contentResolver.call(URI, Key.cleanVirusPackage, null, extras)

        result?.getBoolean(Key.cleanVirusPackage) ?: false
    }

    fun revokePermission(packageName: String) = doAction {
        val extras = Bundle()
            .apply { putString(Key.revokePermission, packageName) }
        val result = currentSystemContext()
            .contentResolver.call(URI, Key.revokePermission, null, extras)

        result?.getBoolean(Key.revokePermission) ?: false
    }

    fun uploadLog(logPayload: LogPayload) = doAction {
        val extras = Bundle()
            .apply { putParcelable(Key.uploadLog, logPayload) }
        val result = currentSystemContext()
            .contentResolver.call(URI, Key.uploadLog, null, extras)

        result?.getBoolean(Key.uploadLog) ?: false
    }

    fun isModuleActive() = doAction {
        val result = currentSystemContext()
            .contentResolver.call(URI, Key.serviceSwitch, null, null)

        result?.getBoolean(Key.serviceSwitch) ?: false
    }

    private fun <R> doAction(action: () -> R): R = runCatching {
        action()
    }.recoverCatching {
        startService()
        action()
    }.getOrThrow()

    private fun startService() {
        val intent = Intent(Const.ACTION_ACTIVE).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        currentSystemContext().startActivity(intent)
    }
}