package com.example.leo.myapplication.net

import de.robv.android.xposed.XC_MethodHook
import java.io.OutputStream
import java.net.URLConnection

object URLConnectionOutputStreamHook : XC_MethodHook() {

    override fun afterHookedMethod(param: MethodHookParam) {
        val urlConnection = param.thisObject as URLConnection

        param.result = MonitorOutputStream(
                urlConnection.url.host,
                urlConnection.url.port,
                param.result as OutputStream
        )
    }
}
