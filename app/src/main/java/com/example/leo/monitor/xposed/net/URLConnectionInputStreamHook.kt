package com.example.leo.monitor.xposed.net

import de.robv.android.xposed.XC_MethodHook
import java.io.InputStream
import java.net.URLConnection

object URLConnectionInputStreamHook : XC_MethodHook() {

    override fun afterHookedMethod(param: MethodHookParam) {
        val result = param.result as? InputStream ?: return
        val urlConnection = param.thisObject as URLConnection

        param.result = MonitorInputStream(
            urlConnection.url.host,
            urlConnection.url.port,
            result
        )
    }
}
