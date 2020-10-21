package com.androidatc.loginapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_fourth.*

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import java.sql.Time


class FourthActivity : AppCompatActivity() {

    var userEmail:String? = null
    var carNum:String? = null
    var milliSec:String? = null

    var finalCharge: Double = 0.00 //to initialize
    var costPerMin : Double = 0.02 //to initialize
    var usageTime : Long = 0

    private val TAG = "FourthActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fourth)

        userEmail = this.intent.getStringExtra("UserEmail")
        carNum = this.intent.getStringExtra("CarNum")
        usageTime = convertStringtoLong(this.intent.getStringExtra("usageTime"))
        Log.d(TAG, "UsageTime=" + usageTime)

        carNum4.text = carNum
        Log.d(TAG, "Car Num=" + carNum)

        //display -> from previous activity
        startTime4.text = this.intent.getStringExtra("StartParkingTime")

        //get new system time as end time
        val endParkingTime = Calendar.getInstance(Locale.ENGLISH)
        val endMilliesTime = endParkingTime.timeInMillis
        endTime4.text = SimpleDateFormat("HH:mm:ss dd-MM-yyyy").format(endParkingTime.time)

        //to convert string milliSec(start time in msec)
        milliSec = this.intent.getStringExtra("startPKTimeInMilSec")  //startPKTimeInMilSec already in millis
        Log.d(TAG, "milliSec="+milliSec)

        val startParkingTime = convertStringtoLong(milliSec) //convert to long type
        var minusOne:Long = -1
        var zero:Long = 0
        if(startParkingTime != minusOne || startParkingTime != zero) {
            //calculate duration = endTime - startTime
            //to convert to minute from millisecond
            val duration = (endMilliesTime - startParkingTime) / 1000 / 60
            //totalTime4.text = duration.toString()
            totalTime4.text = (duration + usageTime).toString()
            //finalCharge  = duration * costPerMin
            finalCharge  = (duration * costPerMin) + (usageTime * costPerMin)

            //Calculate early end Parking Time = startParkingTime + duration(in msec)
            //endParkingTime.timeInMillis = startParkingTime + duration*1000*60
        }

        //take note - for normal flow, timer count down, how to get total charge from end parking time
        totalCharge4.text = finalCharge.toString(); // user interrupt to end parking early
        endTime4.text = SimpleDateFormat("HH:mm:ss dd-MM-yyyy").format(endParkingTime.time)

        //to get user own receipt : only call fifthActivity to display
        btnReceipt.setOnClickListener()
        {
            Log.e(TAG, "Write Transaction To Firebase Database") //to verify if data successfully write to database
            writeTransaction() //write transaction to firebase
            go2FifthActivity()
        }
    }

    //go to fifthActivity
    fun go2FifthActivity()
    {
        var intent = Intent(this, FifthActivity::class.java)
        intent.putExtra("TotalCharge", totalCharge4.text.toString())
        intent.putExtra("duration", totalTime4.text.toString())
        intent.putExtra("StartParkingTime", startTime4.text.toString())
        intent.putExtra("EndParkingTime", endTime4.text.toString())
        intent.putExtra("CarNum", carNum)
        startActivity(intent)
    }

    // to write transaction to firebase -> store
    private fun writeTransaction()
    {
        var transaction = Transaction(userEmail.toString(), startTime4.text.toString(), endTime4.text.toString(),
                totalCharge4.text.toString(), totalTime4.text.toString(), carNum4.text.toString())
        val currentTime = Calendar.getInstance(Locale.ENGLISH)

        //to generate the transaction ID by using the date, month, year, hours, minutes and second
        val tranID = "tranID" + SimpleDateFormat("ddMMyyyyHHmmss").format(currentTime.time)
        Log.d(TAG,"writeTransaction") //for debug purpose
        Log.d(TAG,tranID) //for debug purpose
        Log.d(TAG,transaction.userEmail + " " + transaction.startParkingTime + " " + transaction.duration + " "
            + transaction.totalCharges + " " + transaction.endParkingTime + " " + transaction.carNum)
        Log.d(TAG, "Write transaction to the database")

        // Write transaction to the database
        FirebaseDatabase.getInstance().reference.child("transactions")
                .child(tranID).setValue(transaction).addOnSuccessListener {
                    // Write was successful!
                    Log.d(TAG,"Write Transaction is Successful")
                }
                .addOnFailureListener {
                    // Write failed
                    Log.e(TAG,"Write Transaction is failed")
                }
    }

    //to convert string to long type
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
}