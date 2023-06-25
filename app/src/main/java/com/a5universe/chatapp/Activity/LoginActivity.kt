package com.a5universe.chatapp.Activity

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.a5universe.chatapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    lateinit var auth: FirebaseAuth
    lateinit var database: FirebaseDatabase
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.[a-z]+".toRegex()
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // firebase initialization
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        //progress bar
        progressDialog = ProgressDialog(this).apply {
            setTitle("Please Wait...")
            setMessage("Just a moment...")
        }

        //If user not login
        if (auth.currentUser != null) {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        // method for signup text
        SignupTxtWork()

        // method for login button
        loginButtonWork()


    }

    private fun loginButtonWork() {
        binding.btnLogin.setOnClickListener {
            progressDialog.show()
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPass.text.toString()

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                progressDialog.dismiss()
                Toast.makeText(this, "Invalid Data", Toast.LENGTH_SHORT).show()
            }
            else if (!email.matches(emailPattern)) {
                progressDialog.dismiss()
                binding.loginEmail.error = "Invalid Email"
                Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show()
            }
            else if (password.length < 6) {
                progressDialog.dismiss()
                binding.loginPass.error = "Invalid Password"
                Toast.makeText(this, "Please Enter Valid Password", Toast.LENGTH_SHORT).show()
            }
            else {

                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    progressDialog.dismiss()
                    if (it.isSuccessful) {
                            startActivity(Intent(this, HomeActivity::class.java))
                        } else {
                            Toast.makeText(this, "Login Error", Toast.LENGTH_SHORT).show()
                        }
                }
            }

        }
    }

    private fun SignupTxtWork() {
        binding.txtSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}