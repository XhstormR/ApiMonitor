package com.example.leo.myapplication

data class InstrumentationConfig(
        val packageName: String,
        val hookConfigs: List<HookConfig>
)

data class HookConfig(
        val className: String,
        val methodName: String?
)
