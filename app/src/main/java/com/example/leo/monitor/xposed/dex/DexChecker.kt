package com.example.leo.monitor.xposed.dex

import com.example.leo.monitor.util.clazz
import com.example.leo.monitor.xposed.HookConfig

object DexChecker {
    fun install(hookConfigs: MutableSet<HookConfig>) {
        hookConfigs.addAll(
            listOf(
                HookConfig(clazz<ClassLoader>().name, "loadClass", DexHook)
            )
        )
    }
}
