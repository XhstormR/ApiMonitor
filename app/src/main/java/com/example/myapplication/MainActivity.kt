package com.example.myapplication

import android.app.Activity
import android.widget.Toast
import androidx.annotation.Keep

class MainActivity : Activity() {

    override fun onResume() {
        super.onResume()

        val msg = if (isModuleActive()) "模块已激活" else "模块未激活"
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    @Keep
    private fun isModuleActive() = false
}
