package com.example.leo.myapplication

import de.robv.android.xposed.XC_MethodHook.MethodHookParam

object ParseGenerator {

    fun parseHook(param: MethodHookParam) = mutableMapOf(
            "class" to param.method.declaringClass.name,
            "method" to param.method.name,
            "timestamp" to System.currentTimeMillis()
    )

    fun parseObject(obj: Any?): Any = when (obj) {
        is List<*> -> obj.map { parseObject(it) }
        is Array<*> -> obj.map { parseObject(it) }
        else -> obj.toString()
    }
}
