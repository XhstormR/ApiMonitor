package com.example.leo.myapplication

import de.robv.android.xposed.XC_MethodHook

data class HookConfig(
    val className: String,
    val methodName: String?,
    val callback: XC_MethodHook? = null
)
