package com.example.leo.myapplication

import android.util.Log
import de.robv.android.xposed.XC_MethodHook

object Callback2 : XC_MethodHook() {
    override fun beforeHookedMethod(param: MethodHookParam) {
        if (param.args.isNotEmpty()) {
            Thread.dumpStack()
            Log.i(Const.TAG, param.method.toString())
            param.args
                    .forEach { Log.i(Const.TAG, it.toString()) }
            Log.i(Const.TAG, "----")
        }
    }
}
