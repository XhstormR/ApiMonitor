package com.example.leo.myapplication.xposed

import com.example.leo.myapplication.util.Logger
import com.example.leo.myapplication.util.currentApplicationHash
import com.example.leo.myapplication.xposed.leak.LeakChecker
import de.robv.android.xposed.XC_MethodHook
import org.json.JSONObject

class Tracing(
    private val packageName: String
) : XC_MethodHook() {

    companion object {
        private val applicationHash = currentApplicationHash()
    }

    override fun afterHookedMethod(param: MethodHookParam) {
        val map = ParseGenerator.parseHook(param)
        map["packageName"] = packageName
        map["applicationHash"] = applicationHash
        map["args"] = ParseGenerator.parseObject(param.args)
        map["result"] = ParseGenerator.parseObject(param.result)
        map["this"] = ParseGenerator.parseObject(param.thisObject)
        LeakChecker.parseHook(param)?.let { map["action"] = it }
        Logger.logHook(map)
        BackgroundService.uploadLog(JSONObject(map as Map<*, *>).toString())
    }
}
