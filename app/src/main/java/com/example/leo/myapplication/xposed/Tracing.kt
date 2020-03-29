package com.example.leo.myapplication.xposed

import com.example.leo.myapplication.model.parcel.LogPayload
import com.example.leo.myapplication.util.Logger
import com.example.leo.myapplication.util.currentApplicationHash
import com.example.leo.myapplication.util.toJSONObject
import com.example.leo.myapplication.xposed.leak.LeakChecker
import de.robv.android.xposed.XC_MethodHook

class Tracing(
    private val packageName: String
) : XC_MethodHook() {

    companion object {
        private val appHash = currentApplicationHash()
    }

    override fun afterHookedMethod(param: MethodHookParam) {
        val map = ParseGenerator.parseHook(param)
        map["packageName"] = packageName
        map["applicationHash"] = appHash
        map["args"] = ParseGenerator.parseObject(param.args)
        map["result"] = ParseGenerator.parseObject(param.result)
        map["this"] = ParseGenerator.parseObject(param.thisObject)
        LeakChecker.parseHook(param)?.let { map["action"] = it }
        val json = map.toJSONObject().toString()
        Logger.logHook(json)
        BackgroundService.uploadLog(LogPayload(appHash, json))
    }
}
