package com.example.leo.monitor

object Const {

    const val SELF_Fragment = "${BuildConfig.APPLICATION_ID}.PreferenceFragment"

    const val AUTHORITY = "${BuildConfig.APPLICATION_ID}.CP"

    const val ACTION_ACTIVE = "${BuildConfig.APPLICATION_ID}.ACTION_ACTIVE"

    const val CONFIG_FILENAME = "hooks.json"

    const val LOGTAG = "ApiMonitor"

    const val MSG_DO_FOREGROUND = 1
    const val KEY_DO_FOREGROUND = "KEY_GET_FOREGROUND_APP"
}

object Key {

    const val uploadApk = "uploadApk"

    const val uploadLog = "uploadLog"

    const val revokePermission = "revokePermission"

    const val cleanVirusPackage = "cleanVirusPackage"

    const val hooks = "hooks"

    const val fridaSwitch = "fridaSwitch"

    const val xposedSwitch = "xposedSwitch"

    const val backendSwitch = "backendSwitch"

    const val serviceCategory = "serviceCategory"

    const val assetUpdateTime = "assetUpdateTime"
}
