package com.example.leo.myapplication.xposed.net

import de.robv.android.xposed.XC_MethodHook
import java.io.InputStream
import java.net.URLConnection

object URLConnectionInputStreamHook : XC_MethodHook() {

    override fun afterHookedMethod(param: MethodHookParam) {
        val urlConnection = param.thisObject as URLConnection

        param.result = MonitorInputStream(
            urlConnection.url.host,
            urlConnection.url.port,
            param.result as InputStream
        )
    }
}
