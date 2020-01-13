package com.example.leo.myapplication.leak

import de.robv.android.xposed.XC_MethodHook

object TelephonyManagerCheckHook : XC_MethodHook() {

    override fun afterHookedMethod(param: MethodHookParam) {
        LeakChecker.addSample(Type.IMEI, param.result)
    }
}
