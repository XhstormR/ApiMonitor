package com.example.leo.myapplication

import android.content.Context
import android.util.Log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

class Hook : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        Log.i(Const.TAG, "packageName: ${lpparam.packageName}")
        Log.i(Const.TAG, "----")

        if (lpparam.packageName == Const.SELF_PACKAGE) {
            XposedHelpers.findAndHookMethod(
                    Const.SELF_Activity,
                    lpparam.classLoader,
                    "isModuleActive",
                    XC_MethodReplacement.returnConstant(true)
            )
        }

        if (lpparam.packageName == "mobi.w3studio.apps.android.shsmy.phone") {
            XposedHelpers.findAndHookMethod(
                    "com.stub.StubApp",
                    lpparam.classLoader,
                    "getOrigApplicationContext",
                    Context::class.javaObjectType,
                    Callback1
            )
        }
    }
}
