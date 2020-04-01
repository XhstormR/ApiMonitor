package com.example.leo.monitor.model

import com.squareup.moshi.JsonClass
import de.robv.android.xposed.XC_MethodHook

@JsonClass(generateAdapter = true)
data class HookConfig(
    val className: String,
    val methodName: String?,
    @Transient
    val callback: XC_MethodHook? = null
)
