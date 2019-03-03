package com.example.leo.myapplication

import android.content.Context
import android.util.Log
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import java.security.PublicKey

class Callback1 : XC_MethodHook() {
    override fun afterHookedMethod(param: MethodHookParam) {
        val classLoader = (param.result as Context).classLoader

        val callback2 = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                if (param.args.isNotEmpty()) {
                    Thread.dumpStack()
                    Log.i(Hook.TAG, param.method.toString())
                    Log.i(Hook.TAG, param.args[0].toString())
                    Log.i(Hook.TAG, "----")
                }
            }
        }

        try {
            XposedHelpers.findAndHookMethod(
                    "mobi.w3studio.apps.android.shsmy.phone.utils.RSAUtil",
                    classLoader,
                    "encrypt",
                    String::class.javaObjectType,
                    PublicKey::class.javaObjectType,
                    callback2
            )
        } catch (e: Throwable) {
            Log.i(Hook.TAG, Hook.NOT_FOUND)
        }

        try {
            XposedHelpers.findAndHookMethod(
                    "mobi.w3studio.apps.android.shsmy.phone.utils.DES",
                    classLoader,
                    "encrypt",
                    String::class.javaObjectType,
                    callback2
            )
        } catch (e: Throwable) {
            Log.i(Hook.TAG, Hook.NOT_FOUND)
        }
    }
}