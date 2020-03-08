package com.example.leo.myapplication.net

import de.robv.android.xposed.XC_MethodHook
import java.io.InputStream
import java.net.Socket

object SocketInputStreamHook : XC_MethodHook() {

    override fun afterHookedMethod(param: MethodHookParam) {
        val socket = param.thisObject as Socket

        param.result = MonitorInputStream(
            socket.inetAddress.hostAddress,
            socket.port,
            param.result as InputStream
        )
    }
}
