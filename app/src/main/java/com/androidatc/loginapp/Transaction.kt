package com.androidatc.loginapp

import android.provider.ContactsContract
import java.time.Duration

class Transaction{

    //transaction class
    var userEmail: String? = null
    var startParkingTime: String? = null
    var endParkingTime: String? = null
    var totalCharges: String? = null
    var duration: String? = null
    var carNum: String? = null

    constructor(){
        //default constructor
    }

    constructor(userEmail: String, startParkingTime: String,
                endParkingTime: String, totalCharges: String, duration: String, carNum: String)
    {
        this.userEmail = userEmail
        this.startParkingTime = startParkingTime
        this.endParkingTime = endParkingTime
        this.totalCharges = totalCharges
        this.duration = duration
        this.carNum = carNum
    }
}