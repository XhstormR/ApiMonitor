package com.example.leo.myapplication

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_main)

        val msg = if (isModuleActive()) "模块已启动" else "模块未启动"
        Toast.makeText(this, msg, LENGTH_SHORT).show()
    }

    private fun isModuleActive() = false
}
