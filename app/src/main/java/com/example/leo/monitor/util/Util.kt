package com.example.leo.monitor.util

import android.app.AndroidAppHelper
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.UserHandle
import com.example.leo.monitor.Const
import com.squareup.moshi.Moshi
import de.robv.android.xposed.XposedHelpers
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import okio.ByteString
import okio.GzipSink
import okio.HashingSource
import okio.Okio
import org.json.JSONObject

inline fun <reified T> clazz() = T::class.java

fun ByteArray.toHEX(): String =
    ByteString.of(this, 0, this.size).hex()

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

fun UserHandle.toId() = hashCode()

fun <K, V> Map<K, V>.toJSONObject() =
    JSONObject(this)

fun <K, V> ConcurrentHashMap.KeySetView<K, V>.putIfAbsent(key: K) =
    map.putIfAbsent(key, checkNotNull(mappedValue)) == null

fun currentSystemContext() =
    XposedHelpers.findClass("android.app.ActivityThread", null)
        .let { XposedHelpers.callStaticMethod(it, "currentActivityThread") }
        .let { XposedHelpers.callMethod(it, "getSystemContext") } as Context

fun currentApplicationHash(sourceApk: File = File(AndroidAppHelper.currentApplicationInfo().sourceDir)) =
    HashingSource.sha256(Okio.source(sourceApk))
        .apply { Okio.buffer(this).use { it.readAll(Okio.blackhole()) } }
        .hash().hex()

fun gzip(input: File, output: File) {
    Okio.buffer(Okio.source(input)).use { source ->
        Okio.buffer(GzipSink(Okio.sink(output))).use { sink -> sink.writeAll(source) }
    }
}

val moshi: Moshi = Moshi.Builder().build()

val CP_URI: Uri = Uri.Builder()
    .scheme(ContentResolver.SCHEME_CONTENT)
    .authority(Const.AUTHORITY)
    .build()
