package com.example.leo.myapplication

import android.content.Context
import android.util.Log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

class Hook : IXposedHookLoadPackage {

    companion object {
        const val TAG = "HOOK"
        const val NOT_FOUND = "NOT FOUND"
        private const val SELF_PACKAGE = "com.example.leo.myapplication"
        private const val SELF_Activity = "$SELF_PACKAGE.MainActivity"
    }

    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        Log.i(TAG, "packageName: ${lpparam.packageName}")
        Log.i(TAG, "----")

        if (lpparam.packageName == SELF_PACKAGE) {
            XposedHelpers.findAndHookMethod(
                    SELF_Activity,
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
                    Callback1()
            )
        }
    }
}
