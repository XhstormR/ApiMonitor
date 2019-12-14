package com.example.leo.myapplication

import androidx.annotation.Keep

@Keep
data class HookConfig(
    val className: String,
    val methodName: String?
)
