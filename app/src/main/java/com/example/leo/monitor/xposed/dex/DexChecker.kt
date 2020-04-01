package com.example.leo.monitor.xposed.dex

import com.example.leo.monitor.model.HookConfig
import com.example.leo.monitor.util.clazz

object DexChecker {
    fun install(hookConfigs: MutableSet<HookConfig>) {
        hookConfigs.addAll(
            listOf(
                HookConfig(clazz<ClassLoader>().name, "loadClass", DexHook)
            )
        )
    }
}
