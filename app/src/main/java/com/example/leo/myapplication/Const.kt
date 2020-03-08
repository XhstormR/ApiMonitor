package com.example.leo.myapplication

object Const {
    const val SELF_PACKAGE = "com.example.leo.myapplication"
    const val SELF_Fragment = "$SELF_PACKAGE.PreferenceFragment"

    const val AUTHORITY = "$SELF_PACKAGE.CP"

    const val CONFIG_FILENAME = "hooks.json"

    const val LOGTAG = "ApiMonitor"
}

object Key {
    const val hooks = "hooks"

    const val serviceSwitch = "serviceSwitch"

    const val assetUpdateTime = "assetUpdateTime"
}
