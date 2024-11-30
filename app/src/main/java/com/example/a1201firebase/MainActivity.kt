package com.example.a1201firebase

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {
    val db = Firebase.firestore
    var data : MutableList<Map<String,String>> = ArrayList()
    var dataProvinsi = ArrayList<daftarProvinsi>()
//    lateinit var lvAdapter: ArrayAdapter<daftarProvinsi>
    lateinit var lvAdapter: SimpleAdapter
    lateinit var _etProvinsi : EditText
    lateinit var _etIbukota : EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        readData(db)
        _etProvinsi = findViewById(R.id.etProvinsi)
        _etIbukota = findViewById(R.id.etIbukota)
        val _btSimpan = findViewById<Button>(R.id.btSimpan)
        val  _lvData = findViewById<ListView>(R.id.lvData)
//        lvAdapter = ArrayAdapter(
//            this,
//            android.R.layout.simple_list_item_1,
//            dataProvinsi
//        )
        lvAdapter = SimpleAdapter(
            this,
            data,
            android.R.layout.simple_list_item_2,
            arrayOf<String>("Pro","Ibu"),
            intArrayOf(
                android.R.id.text1,
                android.R.id.text2
            )
        )
        _lvData.adapter = lvAdapter
        _btSimpan.setOnClickListener {

            tambahData(db,_etProvinsi.text.toString(),_etIbukota.text.toString())

        }
    }
    fun tambahData(db: FirebaseFirestore, Provinsi : String, Ibukota : String){
        val dataBaru = daftarProvinsi(Provinsi,Ibukota)
        db.collection("tbProvinsi").add(dataBaru)
            .addOnSuccessListener {
                _etProvinsi.setText("")
                _etIbukota.setText("")
                Log.d("firebase", "data Berhasil Disimpan")
            }
            .addOnFailureListener {
                Log.d("firebase", it.message.toString())
            }
        readData(db)
    }
    fun readData(db: FirebaseFirestore){
        db.collection("tbProvinsi").get()
            .addOnSuccessListener {
                result ->
                dataProvinsi.clear()
                for(document in result){
                    val readData = daftarProvinsi(
                        document.data.get("provinsi").toString(),
                        document.data.get("ibukota").toString()
                    )
                    dataProvinsi.add(readData)
                }
                data.clear()
                dataProvinsi.forEach{
                    val dt : MutableMap<String,String> = HashMap(2)
                    dt["Pro"] = it.provinsi
                    dt["Ibu"] = it.ibukota
                    data.add(dt)
                }
                lvAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener{
                Log.d("firebase", it.message.toString())
            }
    }
}