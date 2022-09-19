package com.example.myjournal

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    lateinit var edtText:EditText
    lateinit var buttonSave:Button
    lateinit var buttonView:Button
    lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edtText = findViewById(R.id.mEdtText)
        buttonSave = findViewById(R.id.mBtnSave)
        buttonView = findViewById(R.id.mBtnView)
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Saving")
        progressDialog.setMessage("Please wait")

        buttonSave.setOnClickListener {
            val text = edtText.text.toString().trim()
            val currentTime = System.currentTimeMillis().toString()

            if (text.isEmpty()){
                edtText.setError("Please enter text")
                edtText.requestFocus()
            }else{
                val userData = Data(text,currentTime)
                val reference = FirebaseDatabase.getInstance().getReference().child("Data/$currentTime")
                progressDialog.show()
                reference.setValue(userData).addOnCompleteListener {
                    task->
                    progressDialog.dismiss()
                    if (task.isSuccessful){
                        Toast.makeText(applicationContext,"Saved successfully",Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(applicationContext,task.exception!!.message,Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        buttonView.setOnClickListener {
            startActivity(Intent(applicationContext,ViewActivity::class.java))
        }

    }
}