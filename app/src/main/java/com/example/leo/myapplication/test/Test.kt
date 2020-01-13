/*
package com.example.leo.myapplication.test

import android.app.AndroidAppHelper
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import com.example.leo.myapplication.Logger
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

object Test {

    private val imei by lazy {
        val telephonyManager = AndroidAppHelper.currentApplication()
                .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        telephonyManager.imei
    }

    fun test(context: Context) {
        t1()
        t2()
        t3()
        t4(context)
        t5(context)
        t6(context)
        t7()
        t8()
        t9()
    }

    private fun t1() {
        val des = DES("12345678")
        val data = "一网通办首页个人办事 "
        val encrypt = des.encrypt(data)
        val decrypt = des.decrypt(encrypt)
        println(encrypt)
        println(decrypt)
        println(data)
        println(data == decrypt)
    }

    private fun t2() {
        thread {
            println(get("http://47.98.135.65:8080/0/main/main.xml"))
            println(get("http://47.98.135.65:8080/0/main/main.xml"))
            println(get("http://47.98.135.65:8080/0/main/main.xml"))
        }
    }

    private fun t3() {
        thread {
            println(post("http://httpbin.org/post", "123456"))
            println(post("http://httpbin.org/post", "123456"))
            println(post("http://httpbin.org/post", "123456"))
        }
    }

    private fun t4(context: Context) {
        context.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                println("123456")
            }
        }, IntentFilter("action1"))
    }

    private fun t5(context: Context) {
        context.startService(Intent(context, MyService::class.java))
    }

    private fun t6(context: Context) {
        context.getExternalFilesDir(null)!!
                .resolve("imei.txt")
                .writeText(imei)
    }

    private fun t7() {
        val smsManager = SmsManager.getDefault()
        val des = DES("12345678")
        val encrypt = des.encrypt(imei)
        smsManager.sendTextMessage("+8610010", null, encrypt, null, null)
    }

    private fun t8() {
        val des = DES("12345678")
        val encrypt = des.encrypt(imei)
        thread {
            println(post("http://httpbin.org/post", encrypt))
            println(post("https://httpbin.org/post", encrypt))
        }
    }

    private fun t9() {
        Logger.logError(Build.VERSION.SDK_INT)
        Logger.logError(URL("http://www.qq.com").openConnection()::class.java.name)
        Logger.logError(URL("https://www.qq.com").openConnection()::class.java.name)
    }

    private fun get(url: String, cookie: String = "") = URL(url)
            .getInputStream(cookie)
            .bufferedReader()
            .use { it.readText() }

    private fun post(url: String, data: String, cookie: String = ""): String {
        val connection = URL(url).openConnection() as HttpURLConnection

        connection.doInput = true
        connection.doOutput = true
        connection.requestMethod = "POST"
        connection.setRequestProperty("Accept", "application/json")
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
        connection.setRequestProperty("Cookie", cookie)

        connection.outputStream.bufferedWriter().use { it.write(data) }
        val ret = connection.inputStream
                .bufferedReader()
                .use { it.readText() }
        connection.disconnect()
        connection.disconnect()

        return ret
    }

    private fun URL.getInputStream(cookie: String = ""): InputStream = this
            .openConnection()
            .apply {
                this.setRequestProperty("Cookie", cookie)
                this.setRequestProperty("User-Agent", "OS/Android:4.4.2 Brand/Xiaomi :MI 6  Display/720*1280 EshiminApp/6.3.1")
            }
            .getInputStream()
}
*/
