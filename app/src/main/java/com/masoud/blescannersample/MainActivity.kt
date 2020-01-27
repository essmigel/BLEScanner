package com.masoud.blescannersample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.masoud.blescanner.BLEScanner

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        BLEScanner(applicationContext).startScan()

    }

}
