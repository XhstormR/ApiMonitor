package com.example.leo.monitor

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.example.leo.monitor.util.CP_URI

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_main)

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val packageInfo = getPackageInfo()

        if (preferences.getLong(Key.assetUpdateTime, -1) != packageInfo.lastUpdateTime) {
            initConfig()
            preferences.edit { putLong(Key.assetUpdateTime, packageInfo.lastUpdateTime) }
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PackageManager.PERMISSION_GRANTED)
        }

        contentResolver.call(CP_URI, Key.backendSwitch, null, null)
        contentResolver.call(CP_URI, Key.fridaSwitch, null, null)
    }

    private fun getPackageInfo() =
        packageManager.getPackageInfo(packageName, PackageManager.GET_CONFIGURATIONS)

    private fun initConfig() {
        resources.openRawResource(R.raw.hooks).use { `in` ->
            filesDir.resolve(Const.CONFIG_FILENAME).outputStream().use { out -> `in`.copyTo(out) }
        }
    }
}
