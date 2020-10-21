package com.androidatc.loginapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import android.util.Log
import android.widget.Toast
import android.graphics.Color
import android.support.constraint.ConstraintLayout
import android.support.design.widget.Snackbar
import com.google.firebase.auth.FirebaseAuth;
import android.text.Editable
import android.text.TextWatcher

class MainActivity : AppCompatActivity() {

    //Create Firebase references
    private var mAuth: FirebaseAuth? = null
    private val TAG = "LogUserToFirebase"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Get a reference to the Firebase auth object to initialize
        mAuth = FirebaseAuth.getInstance()

        btnLogin.isEnabled = false

        //to enable btnLogin if txtEmailAddress and txtPassword is not empty
        if(txtEmailAddress.text.isNotEmpty() && txtPassword.text.isNotEmpty())
        {
            btnLogin.isEnabled = true
        }

        //to verify whether the required field is empty
        btnLogin.setOnClickListener{
            //if not all is not empty. Then user can enter
            if(txtPassword.text.isNotEmpty() && txtEmailAddress.text.isNotEmpty()) {
                //will call the signIn method
                signIn(txtEmailAddress.text.toString(), txtPassword.text.toString())
            }

            //If some of the field is empty. Then user are unable to login and error message will toast to user
            else {
                Toast.makeText(applicationContext, "Please Fill In All Details", Toast.LENGTH_LONG).show()
            }
        }

        btnResetPassword.setOnClickListener{
            //if not all is not empty. Then user can enter
            if(txtEmailAddress.text.isNotEmpty()) {
                //will call the signIn method
                //signIn(txtEmailAddress.text.toString(), txtPassword.text.toString())
                resetPassword(txtEmailAddress.text.toString())
            }

            //If some of the field is empty. Then user are unable to login and error message will toast to user
            else {
                Toast.makeText(applicationContext, "Please Fill In Email Address", Toast.LENGTH_LONG).show()
            }
        }

        //to verify whether the text field is empty
        txtEmailAddress.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if(txtEmailAddress.text.isNotEmpty() && txtPassword.text.isNotEmpty())
                {
                    btnLogin.isEnabled = true
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) { }
        })

        //to verify whether the text field is empty
        txtPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if(txtEmailAddress.text.isNotEmpty() && txtPassword.text.isNotEmpty())
                {
                    btnLogin.isEnabled = true
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) { }
        })
    }


    //to go to second activity
    fun Go2SecondActivity(view: View)
    {
        var intent = Intent(this,SecondActivity::class.java)
        startActivity(intent)
    }

    //go to second activity
    fun Go2SecondActivity()
    {
        var intent = Intent(this,SecondActivity::class.java)
        intent.putExtra("UserEmail", txtEmailAddress.text.toString())
        startActivity(intent)
    }

    //go to register activity
    fun Go2Register(view : View)
    {
        var intent = Intent(this,Register::class.java)
        startActivity(intent)
    }

    //to do snack bar
    /*fun MgSnack(view : View)
    {
        var SnackB: Snackbar = Snackbar.make(findViewById(R.id.action_bar_subtitle), "Your Login Is Successful", Snackbar.LENGTH_LONG).apply {
            setActionTextColor(Color.MAGENTA)
            show()
        }
    }*/

    //Sign in current user -> do verification from database -> authentication
    private fun signIn(email:String, password: String){
        mAuth!!.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if(task.isSuccessful){
                        //update UI with the signed-in user's information
                        //user enter password or email correctly
                        //if user exist in the firebase
                        val user = mAuth!!.currentUser
                        Toast.makeText(applicationContext, "User login successful", Toast.LENGTH_LONG).show()
                        Log.d(TAG, "User login successful : " + email) // to trace the error

                        Go2SecondActivity()
                    }
                    else {
                        //user enter password or email wrongly
                        //user does not exist in the firebase
                        Log.e(TAG, "SignIn: Failed", task.exception) // to trace the error
                        Toast.makeText(applicationContext, "User login failed!", Toast.LENGTH_LONG).show()
                    }
                }
    }

    //for user to reset their password if they forgot or if they want to
    private fun resetPassword(email: String){
        mAuth!!.sendPasswordResetEmail(email)
                .addOnCompleteListener(this) { task ->
                    if(task.isSuccessful){
                        //update UI with the signed-in user's information
                        //user enter password or email correctly
                        //if user exist in the firebase
                        val user = mAuth!!.currentUser
                        Toast.makeText(applicationContext, "Reset Password Is Already Send to User Inbox", Toast.LENGTH_LONG).show()
                        Log.d(TAG, "User login successful : " + email) // to trace the error
                    }
                    else {
                        //user enter password or email wrongly
                        //user does not exist in the firebase
                        Log.e(TAG, "Reset Email Fail", task.exception) // to trace the error
                        Toast.makeText(applicationContext, "User login failed!", Toast.LENGTH_LONG).show()
                    }
                }

    }



}