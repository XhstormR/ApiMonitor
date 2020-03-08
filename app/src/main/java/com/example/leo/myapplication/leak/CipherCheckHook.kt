package com.example.leo.myapplication.leak

import de.robv.android.xposed.XC_MethodHook

object CipherCheckHook : XC_MethodHook() {

    override fun afterHookedMethod(param: MethodHookParam) {
        val bytes = param.args[0]
        val result = param.result

        if (bytes !is ByteArray || result !is ByteArray) return

        Type.values()
            .filter { LeakChecker.check(it, bytes) }
            .forEach { LeakChecker.addSample(it, result) }
    }
}
