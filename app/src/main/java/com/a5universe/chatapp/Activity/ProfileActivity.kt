package com.a5universe.chatapp.Activity

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.a5universe.chatapp.Model.Users
import com.a5universe.chatapp.databinding.ActivityProfileBinding
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
    //private lateinit var btnSave: ImageView
    //private lateinit var settingImg: ImageView
    //private lateinit var settingName: EditText
    //private lateinit var settingStatus: EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private var selectedImageUri: Uri? = null
    private lateinit var email: String
    private lateinit var password: String
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

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        val reference: DatabaseReference = database.reference.child("user").child(auth.uid!!)
        val storageReference: StorageReference = storage.reference.child("upload").child(auth.uid!!)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                email = snapshot.child("email").value.toString()
                password = snapshot.child("password").value.toString()
                val name = snapshot.child("name").value.toString()
                val image = snapshot.child("imageUri").value.toString()

                binding.changeName.setText(name)
                Picasso.get().load(image).into(binding.profileImg)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })

////save button click event
        binding.btnSave.setOnClickListener {
            progressDialog.show()

            val name = binding.changeName.text.toString()
//             binding.profileImg.getText().toString()

            if (selectedImageUri != null) {
                storageReference.putFile(selectedImageUri!!)
                    .addOnCompleteListener { task ->
                        storageReference.downloadUrl.addOnSuccessListener { uri ->
                            val finalImageUri = uri.toString()
                            val users = Users(auth.uid!!, name, email, password, finalImageUri)
                            reference.setValue(users).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    progressDialog.dismiss()
                                    Toast.makeText(this, "Update Successfully", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, HomeActivity::class.java))
                                } else {
                                    progressDialog.dismiss()
                                    Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
            } else {
                val users = Users(auth.uid!!, name, email, password, "")
                reference.setValue(users).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        progressDialog.dismiss()
                        Toast.makeText(this, "Update Successfully", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, HomeActivity::class.java))
                    } else {
                        progressDialog.dismiss()
                        Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

//profile pic change..
      binding.changeImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 10) {
            if (resultCode == RESULT_OK && data != null) {
                selectedImageUri = data.data
                binding.profileImg.setImageURI(selectedImageUri)
            }
        }
    }

}