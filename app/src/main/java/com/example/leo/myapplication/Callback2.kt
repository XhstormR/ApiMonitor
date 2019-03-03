package com.example.leo.myapplication

import android.util.Log
import de.robv.android.xposed.XC_MethodHook

object Callback2 : XC_MethodHook() {
    override fun beforeHookedMethod(param: MethodHookParam) {
        if (param.args.isNotEmpty()) {
            Thread.dumpStack()
            Log.i(Const.TAG, param.method.toString())
            Log.i(Const.TAG, param.args[0].toString())
            Log.i(Const.TAG, "----")
        }
    }
}
