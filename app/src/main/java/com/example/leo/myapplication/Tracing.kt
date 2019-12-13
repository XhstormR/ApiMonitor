package com.example.leo.myapplication

import de.robv.android.xposed.XC_MethodHook

object Tracing : XC_MethodHook() {

    override fun afterHookedMethod(param: MethodHookParam) {
        val map = ParseGenerator.parseHook(param)
        map["args"] = ParseGenerator.parseObject(param.args)
        map["result"] = ParseGenerator.parseObject(param.result)
        map["this"] = ParseGenerator.parseObject(param.thisObject)
        Logger.logHook(map)
    }
}
