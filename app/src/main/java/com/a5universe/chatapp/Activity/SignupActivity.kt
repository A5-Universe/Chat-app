package com.a5universe.chatapp.Activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.a5universe.chatapp.Model.Users

import com.a5universe.chatapp.R.*
import com.a5universe.chatapp.databinding.ActivitySignupBinding
import com.google.android.exoplayer2.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class SignupActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var progressDialog: ProgressDialog
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.[a-z]+".toRegex()
    private  var imageUri: Uri? = null
    private var imgURI: String? = null
    private val PICK_IMAGE_REQUEST = 102



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //signup text click event
        loginTxtWork()

        //firebase initialization
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        //progress bar create
        progressDialog = ProgressDialog(this).apply {
            setTitle("Creating Account")
            setMessage("Please Wait...")
        }

        //profile image set
        binding.txtProfile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        //method for signup button event
        binding.btnSignup.setOnClickListener {
            progressDialog.show()
            SignUpButtonWork()
        }

 }
    private fun loginTxtWork() {
        binding.txtLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST) {
            if (data != null) {
                imageUri = data.data!!
                binding.regImage.setImageURI(imageUri)
            }
        }
    }

    private fun SignUpButtonWork() {

        val name = binding.regName.text.toString()
        val email = binding.regEmail.text.toString()
        val password = binding.regPass.text.toString()

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            progressDialog.dismiss()
            Toast.makeText(this, "Invalid Data", Toast.LENGTH_SHORT).show()
        } else if (!email.matches(emailPattern)) {
            progressDialog.dismiss()
            binding.regEmail.error = "Please Enter Valid Email"
            Toast.makeText(this, "Please Enter Valid Email", Toast.LENGTH_SHORT).show()
        } else if (password.length < 6) {
            progressDialog.dismiss()
            binding.regPass.error = "Please Enter Valid Password"
            Toast.makeText(this, "Please Enter Valid Password", Toast.LENGTH_SHORT).show()
        }
        else {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    val reference: DatabaseReference = database.reference.child("user").child(auth.uid!!)
                    val storageReference: StorageReference = storage.reference.child("upload").child(auth.uid!!)

                    // if image selected this is execute
                    if (imageUri != null) {
                        storageReference.putFile(imageUri!!).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                storageReference.downloadUrl.addOnSuccessListener { uri ->
                                    imgURI = uri.toString()
                                    val users = Users(auth.uid, name, email, password, imgURI)

                                    reference.setValue(users).addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            progressDialog.dismiss()
                                            startActivity(Intent(this, HomeActivity::class.java))
                                            Toast.makeText(this, "Created Successfully", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(this, "Error in Creating a new user", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // if not image select default image is execute
                    else {
                        imgURI = "https://firebasestorage.googleapis.com/v0/b/a5-chat-app.appspot.com/o/user.png?alt=media&token=ab8059c6-a92e-4b63-b459-4d76e8e7566c"
                        val user = Users(auth.uid, name, email, password, imgURI )
                        reference.setValue(user).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                progressDialog.dismiss()
                                startActivity(Intent(this, HomeActivity::class.java))
                                Toast.makeText(this, "Created Successfully", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "Error in Creating a New User", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                else{
                    progressDialog.dismiss()
                    Toast.makeText(this, "Failed to create user", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



}



