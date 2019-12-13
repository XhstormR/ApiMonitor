package com.example.leo.myapplication

import android.util.Log
import org.json.JSONObject

object Logger {
    lateinit var PACKAGENAME: String

    fun logHook(map: Map<String, Any>) {
        Log.i(Const.LOGTAG, "$PACKAGENAME:${JSONObject(map)}")
    }

    fun logError(message: String?) {
        Log.e(Const.LOGTAG, "$PACKAGENAME:$message")
    }
}
