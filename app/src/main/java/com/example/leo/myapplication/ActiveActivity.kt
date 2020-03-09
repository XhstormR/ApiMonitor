package com.example.leo.myapplication

import android.app.Activity
import android.os.Bundle

class ActiveActivity : Activity() {

    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        finish()
    }
}
