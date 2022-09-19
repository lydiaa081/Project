package com.example.myjournal

import android.app.ProgressDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ViewActivity : AppCompatActivity() {
    lateinit var listData:ListView
    lateinit var adapter: CustomAdapter
    lateinit var data:ArrayList<Data>
    lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)
        listData =findViewById(R.id.mListData)
        data = ArrayList()
        adapter = CustomAdapter(this,data)
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Loading")
        progressDialog.setMessage("Please wait")
        val reference = FirebaseDatabase.getInstance().getReference().child("Data")
        progressDialog.show()
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                data.clear()
                for (snap in snapshot.children){
                    var userData = snap.getValue(Data::class.java)
                    data.add(userData!!)
                }
                adapter.notifyDataSetChanged()
                progressDialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "DB locked",Toast.LENGTH_SHORT).show()
            }
        })
        listData.adapter =adapter

        listData.setOnItemClickListener { adapterView, view, i, l ->
            val id = data.get(i).id
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("DELETING RECORD")
            alertDialog.setMessage("Are you sure you want to delete this record")
            alertDialog.setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, i ->

            })
            alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i ->
                val deleteRef = FirebaseDatabase.getInstance().getReference().child("Data/$id")
                deleteRef.removeValue().addOnCompleteListener {
                    task->
                    if (task.isSuccessful){
                        Toast.makeText(applicationContext, "Record deleted successfully", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(applicationContext, "Deletion failed!!", Toast.LENGTH_SHORT).show()
                    }
                }

            })
            alertDialog.create().show()
        }
    }
}