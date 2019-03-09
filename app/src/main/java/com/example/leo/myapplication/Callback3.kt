package com.example.leo.myapplication

import android.util.Log
import de.robv.android.xposed.XC_MethodHook

object Callback3 : XC_MethodHook() {
    override fun afterHookedMethod(param: MethodHookParam) {
        Thread.dumpStack()
        Log.i(Const.TAG, param.method.toString())
        Log.i(Const.TAG, param.result.toString())
        Log.i(Const.TAG, "----")
    }
}
