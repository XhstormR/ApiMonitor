package com.example.leo.myapplication

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_main)

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val packageInfo = getPackageInfo()

        if (preferences.getLong(Key.assetUpdateTime, -1) != packageInfo.lastUpdateTime) {
            initConfig()
            preferences.edit().putLong(Key.assetUpdateTime, packageInfo.lastUpdateTime).apply()
        }
    }

    private fun getPackageInfo() =
        packageManager.getPackageInfo(packageName, PackageManager.GET_CONFIGURATIONS)

    private fun initConfig() {
        resources.openRawResource(R.raw.hooks).use { `in` ->
            filesDir.resolve(Const.CONFIG_FILENAME).outputStream().use { out -> `in`.copyTo(out) }
        }
    }
}
