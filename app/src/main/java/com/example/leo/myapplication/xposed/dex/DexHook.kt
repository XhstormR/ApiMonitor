package com.example.leo.myapplication.xposed.dex

import android.app.AndroidAppHelper
import com.example.leo.myapplication.model.parcel.DexPayload
import com.example.leo.myapplication.util.currentApplicationHash
import com.example.leo.myapplication.xposed.BackgroundService
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import java.io.File
import java.util.concurrent.ConcurrentHashMap

object DexHook : XC_MethodHook() {

    private val sizes = ConcurrentHashMap.newKeySet<Int>()

    private val applicationHash = currentApplicationHash()

    override fun afterHookedMethod(param: MethodHookParam) {
        val bytes = param.result
            ?.let { XposedHelpers.callMethod(it, "getDex") }
            ?.let { XposedHelpers.callMethod(it, "getBytes") as ByteArray } ?: return
        val size = bytes.size
        val file = File(AndroidAppHelper.currentApplicationInfo().dataDir)
            .resolve("$size.dex")
        if (size !in sizes) {
            sizes.add(size)
            BackgroundService.uploadDex(DexPayload(applicationHash, bytes))
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
