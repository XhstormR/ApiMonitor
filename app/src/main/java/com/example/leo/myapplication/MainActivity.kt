package com.example.leo.myapplication

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.Keep

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_main)

        val msg = if (isModuleActive()) "模块已启动" else "模块未启动"
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    @Keep
    private fun isModuleActive() = false
}
