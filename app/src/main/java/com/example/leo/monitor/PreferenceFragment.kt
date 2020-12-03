package com.example.leo.monitor

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.Keep
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.example.leo.monitor.util.CP_URI

class PreferenceFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        findPreference<PreferenceCategory>(Key.serviceCategory)!!.apply {
            isEnabled = isModuleActive()
        }

        findPreference<SwitchPreferenceCompat>(Key.backendSwitch)!!.apply {
            onPreferenceClickListener = Preference.OnPreferenceClickListener {
                context.contentResolver.call(CP_URI, Key.backendSwitch, null, null)
                false
            }
        }

        findPreference<SwitchPreferenceCompat>(Key.fridaSwitch)!!.apply {
            onPreferenceClickListener = Preference.OnPreferenceClickListener {
                context.contentResolver.call(CP_URI, Key.fridaSwitch, null, null)
                false
            }
        }

        val msg = if (isModuleActive()) "模块已激活" else "模块未激活"
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    @Keep
    private fun isModuleActive() = false
}
