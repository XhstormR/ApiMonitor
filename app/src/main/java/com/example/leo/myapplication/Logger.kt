package com.example.leo.myapplication

import android.util.Log
import org.json.JSONObject

object Logger {

    fun logHook(map: Map<String, Any>) =
            Log.i(Const.LOGTAG, JSONObject(map).toString())

    fun logError(msg: Any?) =
            Log.e(Const.LOGTAG, msg.toString())
}
