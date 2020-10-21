package com.androidatc.loginapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register_card.*
import java.text.SimpleDateFormat
import java.util.*

class RegisterCard : AppCompatActivity() {

    var parentActivity: String? = null;
    var userEmail: String? = null;
    var TAG = "RegisterCard" //This Activity Name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_card)

        parentActivity = this.intent.getStringExtra("ParentActivity")
        userEmail = this.intent.getStringExtra("UserEmail")
        txtViewUserEmail.text = userEmail

        //to register user card and car num
        btnRegCard.setOnClickListener{
            //var i : Int = 0; //TVUserName.text.isEmpty() ||
            if( txtCardNum.text.isEmpty() || txtExpDate.text.isEmpty()
                    || txtCVC.text.isEmpty() || txtCarNum.text.isEmpty())
            {
                Toast.makeText(applicationContext, "Please Fill All The Details", Toast.LENGTH_LONG).show()
            }

            //TVUserName.text.isNotEmpty() &&
            if( txtCardNum.text.isNotEmpty() && txtExpDate.text.isNotEmpty()
                    && txtCVC.text.isNotEmpty() && txtCarNum.text.isNotEmpty())
            {
                if(txtCardNum.length() == 16 && txtCVC.length() == 3
                        && isValidExpDateFormat(txtExpDate.text.toString())) //txtCardNum.text
                {
                    //if all is valid -> txtCardNum & txtCVC
                    txtCardNum.isEnabled = false
                    txtExpDate.isEnabled = false
                    txtCVC.isEnabled = false
                    txtCarNum.isEnabled = false

                    writeCard()
                    Toast.makeText(applicationContext, "Your Card Register Is Successful", Toast.LENGTH_SHORT).show()
                }

                else
                {
                    Toast.makeText(applicationContext, "Some of the field is Entered Wrongly", Toast.LENGTH_LONG).show()
                }
            }
        }

        btnBackToPrevious.setOnClickListener{
            if(parentActivity == "Register")
            {
                go2RegisterActivity()
            }

            else
            {
                go2ThirdActivity()
            }
        }
    }

    //to go to register activity
    fun go2RegisterActivity(){
        var intent = Intent(this,Register::class.java)
        intent.putExtra("UserEmail", userEmail)
        startActivity(intent)
    }

    //to go to third activity
    fun go2ThirdActivity(){
        var intent = Intent(this, ThirdActivity::class.java)
        intent.putExtra("UserEmail",this.intent.getStringExtra("UserEmail"))
        intent.putExtra("TotalCharge", this.intent.getStringExtra("TotalCharge"))
        intent.putExtra("duration", this.intent.getStringExtra("duration"))
        intent.putExtra("StartParkingTime", this.intent.getStringExtra("StartParkingTime"))
        intent.putExtra("EndParkingTime", this.intent.getStringExtra("EndParkingTime"))
        intent.putExtra("startPKTimeInMilSec", this.intent.getStringExtra("startPKTimeInMilSec"))
        intent.putExtra("CardNum", txtCardNum.text.toString())
        intent.putExtra("CarNum", txtCarNum.text.toString())
        startActivity(intent)
    }

    private  fun writeCard(){
        var card = Card(userEmail.toString(), txtCardNum.text.toString(), txtExpDate.text.toString(),
                txtCVC.text.toString(), txtCarNum.text.toString())
        val currentTime = Calendar.getInstance(Locale.ENGLISH)
        //val cardID = "cardID" + SimpleDateFormat("yyyyMMddHHmmss").format(currentTime.time)
        val cardID = getUsernameFromEmail(userEmail.toString())
        Log.d(TAG,"writeCard")
        Log.d(TAG,cardID)
        Log.d(TAG,card.userEmail + " " + card.cCardNum + " " + card.expDate + " "
                + card.cvcNum + card.carNum)
        Log.d(TAG, "Write card to the database")

        // Write transaction to the database
        FirebaseDatabase.getInstance().reference.child("cards")
                .child(cardID).setValue(card).addOnSuccessListener {
                    // Write was successful!
                    Log.d(TAG,"Write Card is Successful")
                    Toast.makeText(applicationContext, "Register Card - Success!", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    // Write failed
                    Log.e(TAG,"Write Card is failed")
                    Toast.makeText(applicationContext, "Fail to Register Card", Toast.LENGTH_LONG).show()
                }
    }

    //to get username from user email address
    private fun getUsernameFromEmail(email: String?): String{
        return if(email!!.contains("@")){
            email.split("@". toRegex()).dropLastWhile{ it.isEmpty()}.toTypedArray()[0]
        }else{
            email
        }
    }

    //to check credit card exp date format -> mm/yy
    private fun isValidExpDateFormat(expDate: String?): Boolean {
        return (expDate!!.matches("^\\d{2}\\/\\d{2}\$".toRegex()))
    }
}
