package com.example.leo.monitor.xposed.leak

import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.util.Base64
import com.example.leo.monitor.model.HookConfig
import com.example.leo.monitor.util.clazz
import com.example.leo.monitor.util.contains
import com.example.leo.monitor.util.toHEX
import com.example.leo.monitor.xposed.net.MonitorOutputStream
import de.robv.android.xposed.XC_MethodHook.MethodHookParam
import java.io.FileOutputStream
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import javax.crypto.Cipher

object LeakChecker {

    private val sampleMap = ConcurrentHashMap<Type, CopyOnWriteArrayList<ByteArray>>()

    init {
        Type.values().forEach { sampleMap[it] = CopyOnWriteArrayList() }
    }

    fun addSample(type: Type, sample: Any) = when (sample) {
        is String -> addSample(type, sample)
        is ByteArray -> addSample(type, sample)
        else -> error("")
    }

    private fun addSample(type: Type, sample: String) {
        addSample(type, sample.toByteArray())
    }

    private fun addSample(type: Type, sample: ByteArray) {
        val hex = sample.toHEX().toByteArray()
        val base64 = Base64.encode(sample, Base64.DEFAULT)
        sampleMap[type]!!.addAll(listOf(sample, hex, base64))
    }

    fun install(hookConfigs: MutableSet<HookConfig>) {
        hookConfigs.addAll(
            listOf(
                HookConfig(clazz<Cipher>().name, "doFinal", CipherCheckHook),
                HookConfig(clazz<TelephonyManager>().name, "getImei", TelephonyManagerCheckHook),
                HookConfig(clazz<TelephonyManager>().name, "getDeviceId", TelephonyManagerCheckHook),
                HookConfig(clazz<TelephonyManager>().name, "getLine1Number", TelephonyManagerCheckHook)
            )
        )
    }

    fun check(type: Type, bytes: ByteArray) =
        sampleMap[type]!!.any { bytes.contains(it) }

    fun parseHook(param: MethodHookParam) = when {
        param.thisObject is FileOutputStream && param.method.name == "write" && param.args[0] is ByteArray -> writeFile(param)
        param.thisObject is MonitorOutputStream && param.method.name == "write" && param.args[0] is ByteArray -> writeNet(param)
        param.thisObject is SmsManager && param.method.name == "sendTextMessage" && param.args[2] is String -> sendTextSMS(param)
        param.thisObject is SmsManager && param.method.name == "sendDataMessage" && param.args[3] is ByteArray -> sendDataSMS(param)
        else -> null
    }

    private fun sendTextSMS(param: MethodHookParam): String? {
        val bytes = (param.args[2] as String).toByteArray()

        return Type.values().firstOrNull { check(it, bytes) }?.let { "${it}_sms" }
    }

    private fun sendDataSMS(param: MethodHookParam): String? {
        val bytes = param.args[3] as ByteArray

        return Type.values().firstOrNull { check(it, bytes) }?.let { "${it}_sms" }
    }

    private fun writeNet(param: MethodHookParam): String? {
        val bytes = param.args[0] as ByteArray

        return Type.values().firstOrNull { check(it, bytes) }?.let { "${it}_net" }
    }

    private fun writeFile(param: MethodHookParam): String? {
        val bytes = param.args[0] as ByteArray

        return Type.values().firstOrNull { check(it, bytes) }?.let { "${it}_file" }
    }
}
