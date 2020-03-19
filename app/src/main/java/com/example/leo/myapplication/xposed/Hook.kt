package com.example.leo.myapplication.xposed

import android.app.AndroidAppHelper
import android.content.pm.ApplicationInfo
import com.example.leo.myapplication.BuildConfig
import com.example.leo.myapplication.Const
import com.example.leo.myapplication.util.Logger
import com.example.leo.myapplication.util.clazz
import com.example.leo.myapplication.xposed.dex.DexChecker
import com.example.leo.myapplication.xposed.leak.LeakChecker
import com.example.leo.myapplication.xposed.net.NetChecker
import com.google.gson.Gson
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

class Hook : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        if (lpparam.appInfo == null ||
            (lpparam.appInfo.flags and
                (ApplicationInfo.FLAG_SYSTEM or ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0
        ) return

        if (lpparam.packageName == "com.topjohnwu.magisk" ||
            lpparam.packageName == "de.robv.android.xposed.installer"
        ) return

        if (lpparam.packageName == BuildConfig.APPLICATION_ID) {
            XposedHelpers.findAndHookMethod(
                Const.SELF_Fragment,
                lpparam.classLoader,
                "isModuleActive",
                XC_MethodReplacement.returnConstant(true)
            )
            return
        }

        // BackgroundService.revokePermission(AndroidAppHelper.currentPackageName())

        val moduleActive = BackgroundService.isModuleActive()

        if (!moduleActive) return

        val config = BackgroundService.getConfig()

        val hookConfigs = Gson()
            .fromJson(config, clazz<Array<HookConfig>>())
            .toMutableSet()

        DexChecker.install(hookConfigs)

        NetChecker.install(hookConfigs)

        LeakChecker.install(hookConfigs)

        hookConfigs.forEach { hookConfig ->
            runCatching {
                hook(hookConfig)
            }.recoverCatching {
                hook(hookConfig, lpparam.classLoader)
            }.recoverCatching {
                hook(hookConfig, XposedBridge.BOOTCLASSLOADER)
            }.onFailure {
                Logger.logError(it.message)
            }
        }
    }

    private fun hook(hookConfig: HookConfig) {
        hook(hookConfig, clazz<Hook>().classLoader!!)
    }

    private fun hook(hookConfig: HookConfig, classLoader: ClassLoader) {
        val clazz = XposedHelpers.findClass(hookConfig.className, classLoader)
        val tracing = hookConfig.callback ?: Tracing(AndroidAppHelper.currentPackageName())

        if (hookConfig.methodName != null) {
            XposedBridge.hookAllMethods(clazz, hookConfig.methodName, tracing)
        } else {
            XposedBridge.hookAllConstructors(clazz, tracing)
        }
    }
}
