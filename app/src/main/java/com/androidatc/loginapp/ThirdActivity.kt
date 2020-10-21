package com.androidatc.loginapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.renderscript.Sampler
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_second.*
import kotlinx.android.synthetic.main.activity_third.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timer

class ThirdActivity : AppCompatActivity() {

    var userEmail: String? = null
    var startPKTimeInMilSec: String? = null
    var latestPKTimeInMilSec: String? = null
    var carNum: String? = null
    private var mDatabase: DatabaseReference? = null
    private var TAG = "ThirdActivity"
    var durationTime:String? = null
    private var isCancelled = false
    var usageTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //to pass the previous activity contains(fourthActivity) value to here
        setContentView(R.layout.activity_third)

        TotalCharge3.text = this.intent.getStringExtra("TotalCharge")
        TotalPark3.text = this.intent.getStringExtra("duration")
        durationTime = this.intent.getStringExtra("duration")
        startTime3.text = this.intent.getStringExtra("StartParkingTime")
        endTime3.text = this.intent.getStringExtra("EndParkingTime")
        userEmail = this.intent.getStringExtra("UserEmail")
        startPKTimeInMilSec = this.intent.getStringExtra("startPKTimeInMilSec")
        latestPKTimeInMilSec = this.intent.getStringExtra("latestMinutes")
        carNum = this.intent.getStringExtra("CarNum")
        Log.d(TAG, "onCreate:startPKTimeInMilSec=" + startPKTimeInMilSec)
        Log.d(TAG, "onCreate:Car Num=" + carNum)

        //to convert string to long and hold in the program
        val startPKTimeInMilSec = convertStringtoLong(this.startPKTimeInMilSec)
        val latestPKTimeInMilSec = convertStringtoLong(this.latestPKTimeInMilSec)
        val usageTime = convertStringtoLong(this.intent.getStringExtra("usageTime"))
        Log.d(TAG, "onCreate:usageTime=" + usageTime )

        mDatabase = FirebaseDatabase.getInstance().reference

        //disable so user are required to make payment first then they are able to end parking at any time or the counter will start counting
        btnEndParking.isEnabled = false
        btnAddcCard.isEnabled = true

        isCancelled = false // always start with counter

        //to get vehicle num if user has enter the vehicle details
        if (!this.intent.getStringExtra("CarNum").isNullOrEmpty()) {
            txtVehicleNum4.text = this.intent.getStringExtra("CarNum")
        }

        //to get card num if user has enter the card details
        if (!this.intent.getStringExtra("CardNum").isNullOrEmpty()) {
            //creditcard available
            txtCCardNum.text = this.intent.getStringExtra("CardNum")

            //to do validation even the register card activity has done
            if (txtCCardNum.text.length == 16) {
                btnEndParking.isEnabled = true
                btnAddcCard.isEnabled = false

                //val millisInFuture:Long = convertStringtoLong(durationTime)*60*1000 //in msec
                val millisInFuture = (convertStringtoLong(durationTime)*60*1000) - (usageTime*60*1000)

                // Count down interval 1 minute
                val countDownInterval:Long = 1000 * 60

                // start timer
                timer(millisInFuture,countDownInterval).start()
                //Log.d(TAG,"Start timer to count down duration="+durationTime+"min, interval=1min")
                Log.d(TAG,"Start timer to count down duration="+millisInFuture+"ms, interval=1min")
            }
        } else {
            /*//how to query firebase db to get user credit card number when first login
            var userID = getUsernameFromEmail(userEmail)
            val cardReference = mDatabase!!.child("cards").child(userID)

            val cardListener = object : ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.exists()){
                        val card = dataSnapshot.getValue(Card::class.java)
                        if(card!!.cCardNum.isNullOrEmpty())
                            Log.d(TAG,"cCardNum=" + card!!.cCardNum)
                    }
                }
                override fun onCancelled(p0: DatabaseError) {
                    Log.e(TAG,"onCancelled: Failed to read from database")
                }
            }
            cardReference.addValueEventListener(cardListener)*/
        }

        //user are required to add their card and car num before they can activate the start parking time
        btnAddcCard.setOnClickListener {
            isCancelled = true // to cancel count down timer
            go2RegisterCard()
        }

        //before user make payment, they can cancel and back to previous activity to readjust time
        //if user has make payment and they back to adjust time : time duration will accumulate or minus from the current time they have set
        btnCancelbk2ActPark.setOnClickListener()
        {
            btnAddcCard.isEnabled = true
            btnEndParking.isEnabled = false
            isCancelled = true // to cancel count down timer
            go2SecondActivity()
        }

        //Normal flow : once the count end -> the parking session will end automatically
        //to end parking in advance -> the parking session will end directly and calculate the actual amount for the user
        btnEndParking.setOnClickListener()
        {
            isCancelled = true // to cancel count down timer
            go2fourthActivity()
        }
    }

    //when the parking session end or user has end early -> will go to fourthActivity
    //user will get the update parking info at that page
    fun go2fourthActivity() {
        //var transaction = Transaction(userEmail.toString(), startTime3.text.toString(), endTime3.text.toString(), TotalCharge3.text.toString(), TotalPark3.text.toString())

        //to pass value to fourthActivity
        var intent = Intent(this, FourthActivity::class.java)
        //intent.putExtra("TotalCharge", TotalCharge3.text.toString())
        //intent.putExtra("duration", TotalPark3.text.toString())
        intent.putExtra("StartParkingTime", startTime3.text.toString())
        intent.putExtra("EndParkingTime", endTime3.text.toString())
        intent.putExtra("UserEmail", userEmail)
        intent.putExtra("startPKTimeInMilSec", startPKTimeInMilSec.toString())
        intent.putExtra("latestMinutes", latestPKTimeInMilSec.toString())
        intent.putExtra("CarNum", carNum)
        intent.putExtra("usageTime", this.intent.getStringExtra("usageTime"))
        Log.d(TAG, "GoToFourthActivity:usageTime=" + usageTime)
        Log.d(TAG, "GoToFourthActivity:startPKTimeInMilSec=" + startPKTimeInMilSec)
        startActivity(intent)
    }

    //Go to SecondAcivity
    //when user decide to readjust time or extend their parking time
    fun go2SecondActivity() {
        //to pass value to secondActivity
        var intent = Intent(this, SecondActivity::class.java)
        intent.putExtra("ParentActivity", "ThirdActivity")
        intent.putExtra("UserEmail", userEmail)
        intent.putExtra("startPKTimeInMilSec", startPKTimeInMilSec)
        intent.putExtra("CarNum", carNum)
        intent.putExtra("CardNum", txtCCardNum.text.toString())
        intent.putExtra("TotalCharge", TotalCharge3.text.toString())
        //intent.putExtra("duration", TotalPark3.text.toString())
        intent.putExtra("duration", durationTime)
        intent.putExtra("StartParkingTime", startTime3.text.toString())
        intent.putExtra("EndParkingTime", endTime3.text.toString())
        intent.putExtra("usageTime", usageTime.toString())
        //Log.d(TAG, "go2RegisterCard:startPKTimeInMilSec=" + startPKTimeInMilSec)
        startActivity(intent)
    }

    //go to RegisterCard
    fun go2RegisterCard() {
        //to pass value to registerCard Activity
        var intent = Intent(this, RegisterCard::class.java)
        intent.putExtra("ParentActivity", "ThirdActivity")
        intent.putExtra("UserEmail", userEmail)
        intent.putExtra("startPKTimeInMilSec", startPKTimeInMilSec)
        //Log.d(TAG, "go2RegisterCard:startPKTimeInMilSec=" + startPKTimeInMilSec)
        intent.putExtra("TotalCharge", TotalCharge3.text.toString())
       // intent.putExtra("duration", TotalPark3.text.toString())
        intent.putExtra("duration", durationTime)
        intent.putExtra("StartParkingTime", startTime3.text.toString())
        intent.putExtra("EndParkingTime", endTime3.text.toString())
        startActivity(intent)
    }

    //to get user name from email : user1@gmail.com = user1
    private fun getUsernameFromEmail(email: String?): String {
        return if (email!!.contains("@")) {
            email.split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        } else {
            email
        }
    }

    //convert string to long type
    private fun convertStringtoLong(data: String?): Long{
        try {
            if(!data.isNullOrEmpty()){
                return data!!.toLong()
            }
        }catch (exception: NumberFormatException) {
            return -1
        }
        return 0
    }

    // Method to configure and return an instance of CountDownTimer object
    private fun timer(millisInFuture:Long,countDownInterval:Long):CountDownTimer{
        return object: CountDownTimer(millisInFuture,countDownInterval){
            override fun onTick(millisUntilFinished: Long){
                val timeRemaining = millisUntilFinished / 1000 / 60  // timeRemaining = long or int type

                usageTime = (millisInFuture / 1000 / 60) - timeRemaining
                Log.d(TAG,"timer.onTick, Usage Time="+usageTime+"mins")
                Log.d(TAG,"timer.onTick, timeRemaining="+timeRemaining+"mins")
                Alert3.text = "Remaining Parking Time = " + timeRemaining + " minutes" //dispaly alert message

                //show alert is time is <= 10mins
                if(timeRemaining <= 10){
                    Alert3.text = "Your Parking Time Left " + timeRemaining + " minutes. Please add time if you wish to park longer!"
                }

                if (isCancelled){
                    Log.d(TAG, "timer is cancelled")
                    cancel()
                }
            }

            override fun onFinish() {
                Log.d(TAG,"timer.onFinish")
                //go to end parking = fourthActivity
                Alert3.text = "Parking time finished"
                go2fourthActivity()
            }
        }
    }

    /*//sample code, research from website
    // Method to get days hours minutes seconds from milliseconds
    private fun timeString(millisUntilFinished:Long):String{
        var millisUntilFinished:Long = millisUntilFinished
        val days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished)
        millisUntilFinished -= TimeUnit.DAYS.toMillis(days)

        val hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
        millisUntilFinished -= TimeUnit.HOURS.toMillis(hours)

        val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
        millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes)

        val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)

        // Format the string
        return String.format(
                Locale.getDefault(),
                "%02d day: %02d hour: %02d min: %02d sec",
                days,hours, minutes,seconds
        )
    }
    // Method to configure and return an instance of CountDownTimer object
    private fun timer(millisInFuture:Long,countDownInterval:Long):CountDownTimer{
        return object: CountDownTimer(millisInFuture,countDownInterval){
            override fun onTick(millisUntilFinished: Long){
                val timeRemaining = timeString(millisUntilFinished)
                if (isCancelled){
                    text_view.text = "${text_view.text}\nStopped.(Cancelled)"
                    cancel()
                }else{
                    text_view.text = timeRemaining
                }
            }

            override fun onFinish() {
                text_view.text = "Done"

                button_start.isEnabled = true
                button_stop.isEnabled = false
            }
        }
    }
    */
}

