package com.example.leo.monitor.xposed.leak

import de.robv.android.xposed.XC_MethodHook

object TelephonyManagerCheckHook : XC_MethodHook() {

    override fun afterHookedMethod(param: MethodHookParam) {
        val result = param.result ?: return
        when (param.method.name) {
            "getImei" -> LeakChecker.addSample(Type.IMEI, result)
            "getDeviceId" -> LeakChecker.addSample(Type.IMEI, result)
            "getLine1Number" -> LeakChecker.addSample(Type.PHONE_NUMBER, result)
            else -> throw IllegalArgumentException()
        }
    }
}
