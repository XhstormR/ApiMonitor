package com.example.leo.monitor.xposed.dex

import android.app.AndroidAppHelper
import com.example.leo.monitor.util.putIfAbsent
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import java.io.File
import java.util.concurrent.ConcurrentHashMap

object DexHook : XC_MethodHook() {

    private val sizes = ConcurrentHashMap.newKeySet<Int>()

    private val dir by lazy {
        File(AndroidAppHelper.currentApplicationInfo().dataDir)
            .resolve("dex").apply { mkdirs() }
    }

    override fun afterHookedMethod(param: MethodHookParam) {
        val bytes = param.result
            ?.let { XposedHelpers.callMethod(it, "getDex") }
            ?.let { XposedHelpers.callMethod(it, "getBytes") as ByteArray } ?: return
        val size = bytes.size
        if (sizes.putIfAbsent(size)) {
            dir
                .resolve("$size.dex")
                .writeBytes(bytes)
        }
        // if (!file.exists()) {
        //     file.writeBytes(bytes)
        // }
    }
}

/*
val getDex = Class.forName("java.lang.Class").getDeclaredMethod("getDex")
val getBytes = Class.forName("com.android.dex.Dex").getDeclaredMethod("getBytes")
val clazz = param.result?.let { it as Class<*> } ?: return
val bytes = getBytes.invoke(getDex.invoke(clazz)) as ByteArray
*/
