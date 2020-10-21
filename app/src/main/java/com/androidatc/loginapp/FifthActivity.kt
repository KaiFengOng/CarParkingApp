package com.androidatc.loginapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_fifth.*

class FifthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fifth)

        //to redisplay all details from fourthActivity
        //as electronic receipt
        startTime5.text  = this.intent.getStringExtra("StartParkingTime")
        endTime5.text = this.intent.getStringExtra("EndParkingTime")
        totalTime5.text = this.intent.getStringExtra("duration")
        totalCharge5.text = this.intent.getStringExtra("TotalCharge")
        carNum5.text = this.intent.getStringExtra("CarNum")
    }
}
