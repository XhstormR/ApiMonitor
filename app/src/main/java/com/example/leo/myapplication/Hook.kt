package com.example.leo.myapplication

import android.app.AndroidAppHelper
import android.content.ContentResolver
import android.content.pm.ApplicationInfo
import android.net.Uri
import com.example.leo.myapplication.leak.LeakChecker
import com.example.leo.myapplication.net.NetChecker
import com.example.leo.myapplication.util.clazz
import com.example.leo.myapplication.util.currentSystemContext
import com.google.gson.Gson
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

class Hook : IXposedHookLoadPackage {

    companion object {
        private val URI = Uri.Builder()
            .scheme(ContentResolver.SCHEME_CONTENT)
            .authority(Const.AUTHORITY)
            .build()
    }

    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        if (lpparam.appInfo == null ||
            (lpparam.appInfo.flags and
                (ApplicationInfo.FLAG_SYSTEM or ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0
        ) return

        if (lpparam.packageName == Const.SELF_PACKAGE) {
            XposedHelpers.findAndHookMethod(
                Const.SELF_Fragment,
                lpparam.classLoader,
                "isModuleActive",
                XC_MethodReplacement.returnConstant(true)
            )
            return
        }

        if (!isExpModuleActive()) return

        val hookConfigs = Gson()
            .fromJson(getConfig(), clazz<Array<HookConfig>>())
            .toMutableSet()

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

    private fun isExpModuleActive(): Boolean {
        val result = currentSystemContext()
            .contentResolver.call(URI, Key.serviceSwitch, null, null)

        return result?.getBoolean(Key.serviceSwitch) ?: false
    }

    private fun getConfig(): String {
        val result = currentSystemContext()
            .contentResolver.call(URI, Key.hooks, null, null)

        return result?.getString(Key.hooks) ?: ""
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
