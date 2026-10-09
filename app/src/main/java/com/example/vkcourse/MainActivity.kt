package com.example.vkcourse

import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val greeting = TextView(this).apply {
            setText(R.string.hello_android)
            gravity = Gravity.CENTER
        }
        setContentView(greeting)
    }
}
