package com.a5universe.chatapp.ui.authentication.profile

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.a5universe.chatapp.model.authentication.User
import com.a5universe.chatapp.databinding.ActivityProfileBinding
import com.a5universe.chatapp.ui.home.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso


class ProfileActivity : AppCompatActivity() {

    lateinit var binding:ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private var selectedImageUri: Uri? = null
    private lateinit var image: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var name: String
    private lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // creating progressing bar
        progressDialog = ProgressDialog(this).apply {
            setTitle("Updating Your Information")
            setMessage("Please Wait...")
        }

        //firebase initialization
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        //fetch data firebase
        val reference: DatabaseReference = database.reference.child("user").child(auth.uid!!)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //get data
                email = snapshot.child("email").value.toString()
                password = snapshot.child("password").value.toString()
                name = snapshot.child("name").value.toString()
                image = snapshot.child("imgUri").value.toString()

                //set data
                binding.changeName.setText(name)
                binding.changeEmail.setText(email)
//              binding.changePass.setText(password)

                if (image.isEmpty()) {
                    // Load another image link as a placeholder when image URL is empty or null
                    val placeholderImageUrl = "https://firebasestorage.googleapis.com/v0/b/a5-chat-app.appspot.com/o/user.png?alt=media&token=ab8059c6-a92e-4b63-b459-4d76e8e7566c"
                    Picasso.get().load(placeholderImageUrl).into(binding.userImg)
                } else {
                    Picasso.get().load(image).into(binding.userImg)
                }


            }
            override fun onCancelled(error: DatabaseError) {
              // Handle the error
                Log.e("FirebaseError","Error Occured: ${error.message}")
            }
        })

        // save button click event
        binding.btnSave.setOnClickListener {
            progressDialog.show()
            // method for save data
            saveDataFrom(password)
        }

        //profile pic change
        binding.changeImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10)
        }

        //back button click event
        binding.btnBack.setOnClickListener {
                onBackPressed()
        }

    }
    private fun saveDataFrom(password: String) {
        val name = binding.changeName.text.toString()
        val email = binding.changeEmail.text.toString()
//      val password = binding.changePass.text.toString()

        val reference: DatabaseReference = database.reference.child("user").child(auth.uid!!)
        val storageReference: StorageReference = storage.reference.child("upload").child(auth.uid!!)

        if (selectedImageUri != null) {
            storageReference.putFile(selectedImageUri!!)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        storageReference.downloadUrl.addOnSuccessListener { uri ->
                            val finalImageUri = uri.toString()
                            val users = User(auth.uid!!, name, email, password, finalImageUri, "online")

                            reference.setValue(users).addOnCompleteListener { innerTask ->
                                if (innerTask.isSuccessful) {
                                    progressDialog.dismiss()
                                    Toast.makeText(this, "Update Successfully", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, HomeActivity::class.java))
                                } else {
                                    progressDialog.dismiss()
                                    Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } else {
                        progressDialog.dismiss()
                        Toast.makeText(this, "Image Upload Failed", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            // Handle the case when no new image is selected
            val users = User(auth.uid!!, name, email, password, image, "online") // Pass an empty string for the image URL
            reference.setValue(users).addOnCompleteListener { innerTask ->
                if (innerTask.isSuccessful) {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Update Successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HomeActivity::class.java))
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 10) {
            if (resultCode == RESULT_OK && data != null) {
                selectedImageUri = data.data
                binding.userImg.setImageURI(selectedImageUri)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
    override fun onResume() {
        super.onResume()
        // Set user status to online when activity is resumed
        updateUserStatus("online")
    }
    override fun onPause() {
        super.onPause()
        // Set user status to offline when activity is paused
        updateUserStatus("offline")
    }

    private fun updateUserStatus(status: String) {
        database.reference.child("user").child(auth.uid!!).child("status").setValue(status)
    }

}