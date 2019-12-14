package com.example.leo.myapplication

import android.content.pm.ApplicationInfo
import androidx.annotation.Keep
import com.google.gson.Gson
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import java.io.File

@Keep
class Hook : IXposedHookLoadPackage {

    private lateinit var packageName: String

    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        if (lpparam.appInfo == null ||
                (lpparam.appInfo.flags and
                        (ApplicationInfo.FLAG_SYSTEM or ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0) return

        if (lpparam.packageName == Const.SELF_PACKAGE) {
            XposedHelpers.findAndHookMethod(
                    Const.SELF_Activity,
                    lpparam.classLoader,
                    "isModuleActive",
                    XC_MethodReplacement.returnConstant(true)
            )
        }

        packageName = lpparam.packageName

        val hookConfigs = Gson()
                .fromJson(File(Const.CONFIG_FILE).readText(), clazz<Array<HookConfig>>())

        hookConfigs.forEach { hookConfig ->
            runCatching {
                hook(hookConfig)
            }.recoverCatching {
                hook(hookConfig, lpparam.classLoader)
            }.onFailure {
                Logger.logError(it.message)
            }
        }
    }

    private fun hook(hookConfig: HookConfig) {
        hook(hookConfig, XposedBridge.BOOTCLASSLOADER)
    }

    private fun hook(hookConfig: HookConfig, classLoader: ClassLoader) {
        val clazz = XposedHelpers.findClass(hookConfig.className, classLoader)
        val tracing = Tracing(packageName)

        if (hookConfig.methodName != null) {
            clazz.declaredMethods
                    .filter { it.name == hookConfig.methodName }
                    .forEach { XposedBridge.hookMethod(it, tracing) }
        } else {
            clazz.declaredConstructors
                    .forEach { XposedBridge.hookMethod(it, tracing) }
        }
    }
}
