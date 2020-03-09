package com.example.leo.myapplication.xposed

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import com.example.leo.myapplication.Const
import com.example.leo.myapplication.Key
import com.example.leo.myapplication.util.currentSystemContext

object BackgroundService {

    private val URI = Uri.Builder()
        .scheme(ContentResolver.SCHEME_CONTENT)
        .authority(Const.AUTHORITY)
        .build()

    fun getConfig() = exec { doGetConfig() }

    fun isModuleActive() = exec { doIsModuleActive() }

    private fun <R> exec(block: () -> R): R = runCatching {
        block()
    }.recoverCatching {
        doStartService()
        block()
    }.getOrThrow()

    private fun doIsModuleActive(): Boolean {
        val result = currentSystemContext()
            .contentResolver.call(URI, Key.serviceSwitch, null, null)

        return result?.getBoolean(Key.serviceSwitch) ?: false
    }

    private fun doGetConfig(): String {
        val result = currentSystemContext()
            .contentResolver.call(URI, Key.hooks, null, null)

        return result?.getString(Key.hooks) ?: ""
    }

    private fun doStartService() {
        val intent = Intent(Const.ACTION_ACTIVE).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        currentSystemContext().startActivity(intent)
    }
}
