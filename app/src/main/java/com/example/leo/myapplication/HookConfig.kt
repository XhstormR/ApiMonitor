package com.example.leo.myapplication

import androidx.annotation.Keep
import de.robv.android.xposed.XC_MethodHook

@Keep
data class HookConfig(
    val className: String,
    val methodName: String?,
    val callback: XC_MethodHook? = null
)
