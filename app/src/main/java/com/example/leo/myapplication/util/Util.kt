package com.example.leo.myapplication.util

import android.app.AndroidAppHelper
import android.content.Context
import de.robv.android.xposed.XposedHelpers
import okio.HashingSink
import okio.Okio
import java.io.File

inline fun <reified T> clazz() = T::class.java

fun ByteArray.toHEX() =
    this.joinToString("") { String.format("%02x", it) }

fun ByteArray.contains(bytes: ByteArray) =
    this.indexOf(bytes) != -1

// https://github.com/google/guava/blob/master/guava/src/com/google/common/primitives/Bytes.java#L111
fun ByteArray.indexOf(bytes: ByteArray): Int {
    outer@
    for (i in 0..this.size - bytes.size) {
        for (j in bytes.indices) {
            if (this[i + j] != bytes[j]) continue@outer
        }
        return i
    }
    return -1
}

fun currentSystemContext() =
    XposedHelpers.findClass("android.app.ActivityThread", null)
        .let { XposedHelpers.callStaticMethod(it, "currentActivityThread") }
        .let { XposedHelpers.callMethod(it, "getSystemContext") } as Context

fun currentApplicationHash(sourceApk: File = File(AndroidAppHelper.currentApplicationInfo().sourceDir)) =
    HashingSink.sha256(Okio.blackhole()).use { sink ->
        Okio.buffer(Okio.source(sourceApk)).use { source -> source.readAll(sink) }
        sink.hash().hex()
    }

fun byte2Hex(bytes: ByteArray) = StringBuilder(bytes.size * 2)
    .apply {
        for (byte in bytes) {
            append("%02x".format(byte))
        }
    }
    .toString()
