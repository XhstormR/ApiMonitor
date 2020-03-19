package com.example.leo.myapplication

object Const {

    const val SELF_Fragment = "${BuildConfig.APPLICATION_ID}.PreferenceFragment"

    const val AUTHORITY = "${BuildConfig.APPLICATION_ID}.CP"

    const val ACTION_ACTIVE = "${BuildConfig.APPLICATION_ID}.ACTION_ACTIVE"

    const val CONFIG_FILENAME = "hooks.json"

    const val LOGTAG = "ApiMonitor"
}

object Key {

    const val uploadApk = "uploadApk"

    const val uploadDex = "uploadDex"

    const val uploadLog = "uploadLog"

    const val revokePermission = "revokePermission"

    const val cleanVirusPackage = "cleanVirusPackage"

    const val hooks = "hooks"

    const val serviceSwitch = "serviceSwitch"

    const val assetUpdateTime = "assetUpdateTime"
}
