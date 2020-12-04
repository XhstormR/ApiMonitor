package com.example.leo.monitor.xposed

import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.telecom.TelecomManager
import android.telephony.SmsManager
import com.example.leo.monitor.xposed.net.MonitorInputStream
import com.example.leo.monitor.xposed.net.MonitorOutputStream
import dalvik.system.DexClassLoader
import dalvik.system.PathClassLoader
import de.robv.android.xposed.XC_MethodHook.MethodHookParam
import de.robv.android.xposed.XposedHelpers
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.Socket
import javax.crypto.Cipher
import javax.crypto.Mac
import okio.ByteString

object ParseGenerator {

    fun parseHook(param: MethodHookParam): MutableMap<String, Any> = mutableMapOf(
        "class" to param.method.declaringClass.name,
        "method" to param.method.name,
        "action" to action(param),
        "timestamp" to System.currentTimeMillis()
    )

    private fun action(param: MethodHookParam) = when {
        param.thisObject is Mac && param.method.name == "doFinal" -> mac(param)
        param.thisObject is Cipher && param.method.name == "doFinal" -> cipher(param)
        param.thisObject is Socket && param.method.name == "close" -> close(param)
        param.thisObject is Socket && param.method.name == "connect" -> connect(param)
        param.thisObject is FileInputStream && param.method.name == "read" -> readFile(param)
        param.thisObject is FileOutputStream && param.method.name == "write" -> writeFile(param)
        param.thisObject is FileInputStream && param.method.name == "close" -> closeFile(param)
        param.thisObject is FileOutputStream && param.method.name == "close" -> closeFile(param)
        param.thisObject is MonitorInputStream && param.method.name == "read" -> readNet(param)
        param.thisObject is MonitorOutputStream && param.method.name == "write" -> writeNet(param)
        param.thisObject is SmsManager && param.method.name == "sendDataMessage" -> sendSMS(param)
        param.thisObject is SmsManager && param.method.name == "sendTextMessage" -> sendSMS(param)
        param.thisObject is SmsManager && param.method.name == "sendMultipartTextMessage" -> sendSMS(param)
        param.thisObject is TelecomManager && param.method.name == "placeCall" -> placeCall(param)
        param.thisObject is ContextWrapper && param.method.name == "bindService" -> startService(param)
        param.thisObject is ContextWrapper && param.method.name == "startService" -> startService(param)
        param.thisObject is ContextWrapper && param.method.name == "registerReceiver" -> registerReceiver(param)
        param.thisObject is DexClassLoader && param.method.name == DexClassLoader::class.java.name -> loadDEX(param)
        param.thisObject is PathClassLoader && param.method.name == PathClassLoader::class.java.name -> loadDEX(param)
        else -> "null"
    }

    private fun startService(param: MethodHookParam): String {
        val args = param.args
        val intent = args[0] as Intent

        return "start_${intent.component?.className}"
    }

    private fun registerReceiver(param: MethodHookParam): String {
        val args = param.args
        val intentFilter = args[1] as IntentFilter

        return "broadcast_${intentFilter.actionsIterator().asSequence().toList()}"
    }

    private fun placeCall(param: MethodHookParam): String {
        val args = param.args
        val uri = args[0] as Uri

        return "call_${uri.encodedSchemeSpecificPart}"
    }

    private fun sendSMS(param: MethodHookParam): String {
        val args = param.args

        return "sms_${args[0]}"
    }

    private fun loadDEX(param: MethodHookParam): String {
        val args = param.args

        return "load_${args[0]}"
    }

    private fun readFile(param: MethodHookParam): String {
        val inputStream = param.thisObject as FileInputStream

        val path = inputStream.javaClass.getDeclaredField("path")
            .apply { isAccessible = true }
            .get(inputStream)

        return "read_$path"
    }

    private fun writeFile(param: MethodHookParam): String {
        val outputStream = param.thisObject as FileOutputStream

        val path = outputStream.javaClass.getDeclaredField("path")
            .apply { isAccessible = true }
            .get(outputStream)

        return "write_$path"
    }

    private fun closeFile(param: MethodHookParam): String {
        val path = XposedHelpers.getObjectField(param.thisObject, "path")

        return "close_$path"
    }

    private fun readNet(param: MethodHookParam): String {
        val inputStream = param.thisObject as MonitorInputStream

        return "recieve_${inputStream.hostAddress}_${inputStream.hostName}_${inputStream.port}"
    }

    private fun writeNet(param: MethodHookParam): String {
        val outputStream = param.thisObject as MonitorOutputStream

        return "send_${outputStream.hostAddress}_${outputStream.hostName}_${outputStream.port}"
    }

    private fun connect(param: MethodHookParam): String {
        val socket = param.thisObject as Socket
        if (!socket.isConnected) return "open_unconnected"

        return "open_${socket.inetAddress.hostAddress}_${socket.inetAddress.hostName}_${socket.port}"
    }

    private fun close(param: MethodHookParam): String {
        val socket = param.thisObject as Socket
        if (!socket.isConnected) return "close_unconnected"

        return "close_${socket.inetAddress.hostAddress}_${socket.inetAddress.hostName}_${socket.port}"
    }

    private fun mac(param: MethodHookParam): String {
        val mac = param.thisObject as Mac

        return "keyalgo_${mac.algorithm}"
    }

    private fun cipher(param: MethodHookParam): String {
        val cipher = param.thisObject as Cipher
        val opmode = XposedHelpers.getIntField(cipher, "opmode")
        val action = when (opmode) {
            Cipher.ENCRYPT_MODE -> "encryption"
            Cipher.DECRYPT_MODE -> "decryption"
            else -> "unknown"
        }

        return "${action}_${cipher.algorithm}"
    }

    private fun parseByteArray(obj: ByteArray) =
        if (obj.size > DEFAULT_BUFFER_SIZE) obj.toString()
        else ByteString.of(obj, 0, obj.size).base64()

    fun parseObject(obj: Any?): Any = when (obj) {
        is List<*> -> obj.map { parseObject(it) }
        is Array<*> -> obj.map { parseObject(it) }
        is ByteArray -> parseByteArray(obj)
        else -> obj.toString()
    }
}
