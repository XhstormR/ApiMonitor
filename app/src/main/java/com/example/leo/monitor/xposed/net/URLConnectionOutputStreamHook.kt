package com.example.leo.monitor.xposed.net

import de.robv.android.xposed.XC_MethodHook
import java.io.OutputStream
import java.net.InetAddress
import java.net.URLConnection

object URLConnectionOutputStreamHook : XC_MethodHook() {

    override fun afterHookedMethod(param: MethodHookParam) {
        val result = param.result as? OutputStream ?: return
        val urlConnection = param.thisObject as URLConnection
        val inetAddress = InetAddress.getByName(urlConnection.url.host)

        param.result = MonitorOutputStream(
            inetAddress.hostAddress,
            inetAddress.hostName,
            urlConnection.url.port,
            result
        )
    }
}
