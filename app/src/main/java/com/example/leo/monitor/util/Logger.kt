package com.example.leo.monitor.util

import android.util.Log
import com.example.leo.monitor.Const

object Logger {

    fun logHook(msg: String) =
        Log.i(Const.LOGTAG, msg)

    fun logError(msg: Any?, tr: Throwable? = null) =
        Log.e(Const.LOGTAG, msg.toString(), tr)
}
