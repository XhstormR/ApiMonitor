package com.example.leo.myapplication

import android.content.Context
import android.util.Log
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import java.security.PublicKey

object Callback1 : XC_MethodHook() {
    override fun afterHookedMethod(param: MethodHookParam) {
        val classLoader = (param.result as Context).classLoader

        try {
            XposedHelpers.findAndHookMethod(
                    "mobi.w3studio.apps.android.shsmy.phone.utils.RSAUtil",
                    classLoader,
                    "encrypt",
                    String::class.javaObjectType,
                    PublicKey::class.javaObjectType,
                    Tracing
            )
        } catch (e: Throwable) {
            Log.i(Const.TAG, e.message ?: Const.NOT_FOUND)
        }

        try {
            XposedHelpers.findAndHookMethod(
                    "mobi.w3studio.apps.android.shsmy.phone.utils.DES",
                    classLoader,
                    "encrypt",
                    String::class.javaObjectType,
                    Tracing
            )
        } catch (e: Throwable) {
            Log.i(Const.TAG, e.message ?: Const.NOT_FOUND)
        }
    }
}
