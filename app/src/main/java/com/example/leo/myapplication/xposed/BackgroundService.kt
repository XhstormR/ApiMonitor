package com.example.leo.myapplication.xposed

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.example.leo.myapplication.Const
import com.example.leo.myapplication.Key
import com.example.leo.myapplication.model.parcel.DexPayload
import com.example.leo.myapplication.model.parcel.VirusProcess
import com.example.leo.myapplication.util.currentSystemContext

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

    fun uploadDex(dexPayload: DexPayload) = doAction {
        val extras = Bundle()
            .apply { putParcelable(Key.uploadDex, dexPayload) }
        val result = currentSystemContext()
            .contentResolver.call(URI, Key.uploadDex, null, extras)

        result?.getBoolean(Key.uploadDex) ?: false
    }

    fun uploadLog(logPayload: String) = doAction {
        val extras = Bundle()
            .apply { putString(Key.uploadLog, logPayload) }
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
