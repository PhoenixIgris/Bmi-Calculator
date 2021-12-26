package com.igris.bmicalculator

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.igris.bmicalculator.databinding.SplashLayoutBinding
import java.lang.Exception

class SplashActivity:  AppCompatActivity()  {
private lateinit var binding: SplashLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SplashLayoutBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val background: Thread = object : Thread() {
            override fun run() {
                try {
                    // Thread will sleep for 5 seconds
                    sleep(3000)

                    // After 5 seconds redirect to another intent
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                } catch (e: Exception) {
                    Log.e(TAG, "Exception in Splash Activity", )
                }
            }
        }

        background.start()
    }
}