package com.example.leo.myapplication

import android.util.Log
import de.robv.android.xposed.XC_MethodHook

object Tracing : XC_MethodHook() {
    override fun beforeHookedMethod(param: MethodHookParam) {
        trace(param)
        val args = param.args?.contentToString() ?: "void"
        Log.i(Const.TAG, "args: $args")
        Log.i(Const.TAG, "----")
    }

    override fun afterHookedMethod(param: MethodHookParam) {
        trace(param)
        val result = param.result?.toString() ?: "void"
        Log.i(Const.TAG, "result: $result")
        Log.i(Const.TAG, "----")
    }

    private fun trace(param: MethodHookParam) {
        Log.i(Const.TAG, "Stack trace:", Exception())
        Log.i(Const.TAG, param.method.toString())
    }
}
