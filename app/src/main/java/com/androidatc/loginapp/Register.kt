package com.androidatc.loginapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.view.ViewParentCompat
import android.view.View
import android.widget.Toast
import android.util.Log
import kotlinx.android.synthetic.main.activity_register.*


//interface to Firebase
import com.google.firebase.auth.FirebaseAuth;

public class Register : AppCompatActivity() {

    //Create Firebase references
    private var mAuth: FirebaseAuth? = null
    private val TAG = "RegisterUserToFirebase"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //Get a reference to the Firebase auth object
        mAuth = FirebaseAuth.getInstance();


        btnCancel.setOnClickListener{
            txtPasswordReg.setText("");
            txtEmail.setText("");
            Go2MainActivity()
        }

        btnConfirm.setOnClickListener{
            if(txtPasswordReg.text.isNotEmpty() && txtEmail.text.isNotEmpty()) {
                createAccount(txtEmail.text.toString(), txtPasswordReg.text.toString()) // directly get from user input

            }
            else {
                Toast.makeText(applicationContext, "Please Fill All Details", Toast.LENGTH_LONG).show()
            }
        }

        btnRegisterCard.setOnClickListener{
            Go2RegisterCard()
        }
    }

    fun Go2MainActivity(){
        var intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    /*
    fun Go2MainActivity(view : View)
    {
        var intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }*/

    fun Go2RegisterCard()
    {
        var intent= Intent(this,RegisterCard::class.java)
        intent.putExtra("ParentActivity","Register")
        intent.putExtra("UserEmail", txtEmail.text.toString())
        startActivity(intent)
    }

    //Create new user account
    private fun createAccount(email: String, password: String){
        Log.e(TAG, "createAccount:" + email) //for debug purpose
        mAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if(task.isSuccessful){
                        Log.d(TAG, "createAccount: Success!")
                        val user = mAuth!!.currentUser
                        if(user !=null) {
                        }
                        Toast.makeText(applicationContext, "createAcount: Success!", Toast.LENGTH_LONG).show()
                        txtPasswordReg.isEnabled = false
                        txtEmail.isEnabled = false

                    }
                    //if user exist in firebase
                    //also perform format check
                    else{
                        Log.e(TAG, "createAccount: Failed", task.exception)
                        Toast.makeText(applicationContext, "createAcount: Failed!", Toast.LENGTH_LONG).show()
                    }
                }
    }


    /*
    private fun signIn(email:String, password: String){
        mAuth!!.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if(task.isSuccessful){
                        //update UI with the signed-in user's information
                        val user = mAuth!!.currentUser
                        Toast.makeText(applicationContext, "New account created & authentication successful", Toast.LENGTH_LONG).show()
                    }
                    else {
                        Toast.makeText(applicationContext, "Authentication failed!", Toast.LENGTH_LONG).show()
                    }
                }
    }
    */



    //to store user email and password
    /*private fun sendEmailVerification(){
        val user = mAuth!!.currentUser
        user!!.sendEmailVerification()
                .addOnCompleteListener(this){ task ->
                    if(task.isSuccessful){
                        Toast.makeText(applicationContext, "Verification email sent to " + user.email!!, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(applicationContext, "Failed to send verification email.", Toast.LENGTH_LONG).show()
                    }
                }
    }*/

    /*
    private fun signOut(){
        mAuth!!.signOut()
    }
    */
}
