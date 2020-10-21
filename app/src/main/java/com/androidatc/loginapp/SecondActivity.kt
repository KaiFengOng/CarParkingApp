package com.androidatc.loginapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import android.util.Log

import kotlinx.android.synthetic.main.activity_second.*
import java.text.SimpleDateFormat
import java.util.*


class SecondActivity : AppCompatActivity() {

    var defaultParkingTime : Int = 30;  // can be any time, but use 15 minutes -> change to 60min which is 1 hour
    var counter : Int = 0;
    var cost : Double = 0.02;
    var totalCharge : Double = 0.0;
    var latestMinutes : Long = 0; //end time
    var userEmail:String? = null  //to hold user input from previous page
    var carNum:String? = null  //to hold user input from previous page
    var startPKTimeInMilSec: String? = null  //to convert start parking time to milli second
    var latestPKTimeInMilSec: String? = null //to convert latest parking time to milli second

    private val TAG = "SecondActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        //Default from login activity, which is after user login successful
        userEmail = this.intent.getStringExtra("UserEmail")
        Minutes2.setText(counter.toString())
        val startParkingTime = Calendar.getInstance(Locale.ENGLISH)
        val millis = startParkingTime.timeInMillis // to convert start parking time to millis
        SystemClock2.text = SimpleDateFormat("HH:mm:ss dd-MM-yyyy").format(startParkingTime.time) // to display start parking time in time and date format

        //pass original time to this activity for readjustment from third Activity
        //for new adjustment time
        var parentActivityName = this.intent.getStringExtra("ParentActivity")
        if(!parentActivityName.isNullOrEmpty() && parentActivityName == "ThirdActivity")
        {
            txtTotalPay2.text = this.intent.getStringExtra("TotalCharge")
            Minutes2.text = this.intent.getStringExtra("duration")
            var durationTime = this.intent.getStringExtra("duration")
            //convert durationTime to Int
            counter = convertStringtoInt(durationTime)
            SystemClock2.text = this.intent.getStringExtra("StartParkingTime")
            EstimateEndTime2.text = this.intent.getStringExtra("EndParkingTime")
            userEmail = this.intent.getStringExtra("UserEmail")
            startPKTimeInMilSec = this.intent.getStringExtra("startPKTimeInMilSec")
            latestPKTimeInMilSec = this.intent.getStringExtra("latestMinutes")
            carNum = this.intent.getStringExtra("CarNum")
            //Log.d(TAG, "onCreate:startPKTimeInMilSec=" + startPKTimeInMilSec)
            //Log.d(TAG, "onCreate:Car Num=" + carNum)
        }

        //to disable minus button, to prevent user to click on it
        //will enable once the user click on the btnPlus
        if(counter < defaultParkingTime) {
            btnMinus.isEnabled = false
        }

        //to make adjustment on the user park time
        btnMinus.setOnClickListener{
            counter -= defaultParkingTime

            Minutes2.text = counter.toString() // to display updated minutes for user
            totalCharge = cost * counter  // calculate total charge
            txtTotalPay2.text = totalCharge.toString() + "0"; // display

            if(counter < defaultParkingTime)  //defaultParkingTime = 30
            {
                //to disable minus button -> so user can minus the time when the counter reach 0
                btnMinus.isEnabled = false
                Toast.makeText(application, "Your Park Time Must Be At Least 15 Minutes", Toast.LENGTH_LONG).show()
            }

            // to convert time in milliseconds (1sec=1000msec, 1min=60sec)
            latestMinutes = millis + (counter * 60 * 1000)

            var calendar = Calendar.getInstance();  // to declare the calendar variable
            calendar.timeInMillis = latestMinutes // to get latest minute in milli second from calendar
            var endTimeText = SimpleDateFormat("HH:mm:ss dd-MM-yyyy").format(calendar.time)  // to display time in format
            EstimateEndTime2.text = endTimeText   // to display estimate end time
            Log.e(TAG, endTimeText)  // to log the time, for debug purpose
        }

        //to make adjustment on the user park time
        btnPlus.setOnClickListener{
            counter += defaultParkingTime

            if(counter > 0) {
                //to enable minus button
                btnMinus.isEnabled = true
            }

            Minutes2.text = counter.toString() // display update minutes for user
            totalCharge = cost * counter   // calculate total charge
            txtTotalPay2.text = "RM " + totalCharge.toString() + "0" // display total estimate charge for user

            latestMinutes = millis + (counter * 60 * 1000) // to convert time in milliseconds

            var calendar = Calendar.getInstance()
            calendar.timeInMillis = latestMinutes  // to convert milliseocnds back to hh:mm:ss dd-mm-yyyy
            var endTimeText = SimpleDateFormat("HH:mm:ss dd-MM-yyyy").format(calendar.time)  // to display time in format
            EstimateEndTime2.text = endTimeText  // to display estimate end time
            Log.e(TAG, endTimeText)
        }

        //after user satisfied -> then tey proceed
        btnProceed.setOnClickListener {
            // to verify: if user set park time is 0. Then they can't park
            // cannot make payment process
            if(txtTotalPay2.text.isEmpty() || Minutes2.text == "0")
            {
                Toast.makeText(application, "Your Park Time must be at least 1 hour", Toast.LENGTH_LONG).show()
            }

            // they are able to make payment -> to third activity
            else
            {
                //to pass all value to third activity
                var intent = Intent (this, ThirdActivity::class.java) //to go to thirdActivity
                intent.putExtra("TotalCharge", txtTotalPay2.text.toString())
                intent.putExtra("StartParkingTime", SystemClock2.text.toString())
                intent.putExtra("startPKTimeInMilSec", millis.toString())
                intent.putExtra("duration", Minutes2.text.toString())
                intent.putExtra("UserEmail", userEmail)

                //Log.d(TAG,"PayButtonClick:millis"+millis)
                var calendar = Calendar.getInstance()

                calendar.timeInMillis = latestMinutes
                var endEstimateTime = SimpleDateFormat("HH:mm:ss dd-MM-yyyy").format(calendar.time)

                intent.putExtra("EndParkingTime", endEstimateTime)
                intent.putExtra("latestMinutes", latestMinutes.toString())
                intent.putExtra("CarNum", this.intent.getStringExtra("CarNum"))
                intent.putExtra("CardNum", this.intent.getStringExtra("CardNum"))
                intent.putExtra("usageTime", this.intent.getStringExtra("usageTime"))
                startActivity(intent)
            }
        }
    }

    /*fun Go2thirdActivity(view: View) {
        var intent = Intent(this, ThirdActivity::class.java)
        startActivity(intent)
    }*/

    // to convert string to integer
    private fun convertStringtoInt(data: String?): Int{
        try {
            if(!data.isNullOrEmpty()){
                return data!!.toInt()
            }
        }catch (exception: NumberFormatException) {
            return -1
        }
        return 0
    }
}