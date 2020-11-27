package com.example.leo.monitor.xposed

import android.content.Intent
import android.os.Bundle
import com.example.leo.monitor.Const
import com.example.leo.monitor.Key
import com.example.leo.monitor.model.parcel.LogPayload
import com.example.leo.monitor.model.parcel.VirusProcess
import com.example.leo.monitor.util.CP_URI
import com.example.leo.monitor.util.currentSystemContext

object BackgroundService {

    fun getConfig() = doAction {
        val result = currentSystemContext()
            .contentResolver.call(CP_URI, Key.hooks, null, null)

        result?.getString(Key.hooks) ?: ""
    }

    fun cleanVirusPackage(virusProcess: VirusProcess) = doAction {
        val extras = Bundle()
            .apply { putParcelable(Key.cleanVirusPackage, virusProcess) }
        val result = currentSystemContext()
            .contentResolver.call(CP_URI, Key.cleanVirusPackage, null, extras)

        result?.getBoolean(Key.cleanVirusPackage) ?: false
    }

    fun revokePermission(packageName: String) = doAction {
        val extras = Bundle()
            .apply { putString(Key.revokePermission, packageName) }
        val result = currentSystemContext()
            .contentResolver.call(CP_URI, Key.revokePermission, null, extras)

        result?.getBoolean(Key.revokePermission) ?: false
    }

    fun uploadLog(logPayload: LogPayload) = doAction {
        val extras = Bundle()
            .apply { putParcelable(Key.uploadLog, logPayload) }
        val result = currentSystemContext()
            .contentResolver.call(CP_URI, Key.uploadLog, null, extras)

        result?.getBoolean(Key.uploadLog) ?: false
    }

    fun isModuleActive() = doAction {
        val result = currentSystemContext()
            .contentResolver.call(CP_URI, Key.xposedSwitch, null, null)

        result?.getBoolean(Key.xposedSwitch) ?: false
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
