package com.example.leo.monitor.xposed.net

import de.robv.android.xposed.XC_MethodHook
import java.io.InputStream
import java.net.InetAddress
import java.net.URLConnection

object URLConnectionInputStreamHook : XC_MethodHook() {

    override fun afterHookedMethod(param: MethodHookParam) {
        val result = param.result as? InputStream ?: return
        val urlConnection = param.thisObject as URLConnection
        val inetAddress = InetAddress.getByName(urlConnection.url.host)

        param.result = MonitorInputStream(
            inetAddress.hostAddress,
            inetAddress.hostName,
            urlConnection.url.port,
            result
        )
    }
}
