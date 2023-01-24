package com.example.myapplication

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

class Hook : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        XposedBridge.log("Hook -> ${lpparam.packageName}")

        when (lpparam.packageName) {
            BuildConfig.APPLICATION_ID -> {
                XposedHelpers.findAndHookMethod(
                    MainActivity::class.qualifiedName,
                    lpparam.classLoader,
                    "isModuleActive",
                    XC_MethodReplacement.returnConstant(true)
                )
            }
            "com.dataviz.docstogo" -> hookDocstogo(lpparam)
            else -> return
        }
    }

    fun hookDocstogo(lpparam: LoadPackageParam) {
        XposedHelpers.findAndHookMethod(
            "com.dataviz.dxtg.common.android.iap.b",
            lpparam.classLoader,
            "c",
            XC_MethodReplacement.returnConstant(true)
        )
        XposedHelpers.findAndHookMethod(
            "k0.a",
            lpparam.classLoader,
            "b",
            XC_MethodReplacement.returnConstant(true)
        )
        XposedHelpers.findAndHookMethod(
            "k0.b",
            lpparam.classLoader,
            "a",
            XC_MethodReplacement.returnConstant(true)
        )
    }
}
