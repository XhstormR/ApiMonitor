package com.example.leo.myapplication.xposed.dex

import com.example.leo.myapplication.util.clazz
import com.example.leo.myapplication.xposed.HookConfig

object DexChecker {
    fun install(hookConfigs: MutableSet<HookConfig>) {
        hookConfigs.addAll(
            listOf(
                HookConfig(clazz<ClassLoader>().name, "loadClass", DexHook)
            )
        )
    }
}
