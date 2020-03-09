package com.example.leo.myapplication.xposed.leak

import de.robv.android.xposed.XC_MethodHook

object TelephonyManagerCheckHook : XC_MethodHook() {

    override fun afterHookedMethod(param: MethodHookParam) {
        when (param.method.name) {
            "getImei" -> LeakChecker.addSample(Type.IMEI, param.result)
            "getDeviceId" -> LeakChecker.addSample(Type.IMEI, param.result)
            "getLine1Number" -> LeakChecker.addSample(Type.PHONE_NUMBER, param.result)
            else -> throw IllegalArgumentException()
        }
    }
}
