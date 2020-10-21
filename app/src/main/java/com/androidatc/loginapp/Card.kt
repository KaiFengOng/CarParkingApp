package com.androidatc.loginapp

class Card {

    //card class
    var userEmail: String? = null
    var cCardNum: String? = null
    var expDate: String? = null
    var cvcNum: String? = null
    var carNum: String? = null

    constructor(userEmail: String, cCardNum: String, expDate: String, cvcNum: String, carNum: String)
    {
        this.userEmail = userEmail
        this.cCardNum = cCardNum
        this.expDate = expDate
        this.cvcNum = cvcNum
        this.carNum = carNum
    }

    constructor() {
        //default constructor
    }
}