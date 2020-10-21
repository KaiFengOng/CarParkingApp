package com.androidatc.loginapp

class CustomerDetails {

    //customer Details
    var Name: String? = null
    var Contact: String? = null
    var LicensePlate: String? = null
    var OKU: String? = null
    var carType: String? = null
    var email: String? = null


    constructor(Name: String, Contact: String, LicensePlate: String, OKU: String, carType: String, email: String)
    {
        this.Name = Name
        this.Contact = Contact
        this.LicensePlate = LicensePlate
        this.OKU = OKU
        this.carType = carType
        this.email = email
    }

    constructor() {//Default Constructor
    }
}