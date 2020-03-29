package com.example.leo.monitor

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.Keep
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat

class PreferenceFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        findPreference<SwitchPreferenceCompat>(Key.serviceSwitch)!!.apply {
            isEnabled = isModuleActive()
        }

        val msg = if (isModuleActive()) "模块已激活" else "模块未激活"
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    @Keep
    private fun isModuleActive() = false
}
