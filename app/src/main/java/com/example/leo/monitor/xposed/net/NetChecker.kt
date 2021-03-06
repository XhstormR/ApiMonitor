package com.example.leo.monitor.xposed.net

import com.example.leo.monitor.model.HookConfig
import com.example.leo.monitor.util.clazz
import java.net.Socket

object NetChecker {
    fun install(hookConfigs: MutableSet<HookConfig>) {
        hookConfigs.addAll(
            listOf(
                HookConfig(clazz<MonitorInputStream>().name, "read"),
                HookConfig(clazz<MonitorOutputStream>().name, "write"),
                HookConfig(clazz<Socket>().name, "getInputStream", SocketInputStreamHook),
                HookConfig(clazz<Socket>().name, "getOutputStream", SocketOutputStreamHook),
                HookConfig("com.android.okhttp.internal.huc.HttpURLConnectionImpl", "getInputStream", URLConnectionInputStreamHook),
                HookConfig("com.android.okhttp.internal.huc.HttpURLConnectionImpl", "getOutputStream", URLConnectionOutputStreamHook),
                HookConfig("com.android.okhttp.internal.huc.HttpsURLConnectionImpl", "getInputStream", URLConnectionInputStreamHook),
                HookConfig("com.android.okhttp.internal.huc.HttpsURLConnectionImpl", "getOutputStream", URLConnectionOutputStreamHook)
            )
        )
    }
}
