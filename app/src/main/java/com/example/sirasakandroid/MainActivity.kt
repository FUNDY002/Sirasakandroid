package com.example.sirasakandroid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sirasakandroid.Adddata
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import housesAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.reflect.Type

class MainActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    private lateinit var recyclerView: RecyclerView
    private lateinit var housesAdapter: housesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        housesAdapter = housesAdapter(emptyList())
        recyclerView.adapter = housesAdapter

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val addDataButton = findViewById<Button>(R.id.btnsavedata)
        addDataButton.setOnClickListener {
            val intent = Intent(this, Adddata::class.java)
            startActivity(intent)
        }

        val viewButton = findViewById<Button>(R.id.btnview)
        viewButton.setOnClickListener {
            fetchData()
        }

        fetchData()
    }

    private fun fetchData() {
        CoroutineScope(Dispatchers.IO).launch {
            val url = getString(R.string.root_url) + getString(R.string.fetchdata)
            val request = Request.Builder()
                .url(url)
                .build()

            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""

                Log.d("ResponseBody", responseBody)

                withContext(Dispatchers.Main) {
                    handleFetchResponse(responseBody)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun handleFetchResponse(responseBody: String) {
        try {
            val housesListType: Type = object : TypeToken<List<houses>>() {}.type
            val houses: List<houses> = Gson().fromJson(responseBody, housesListType)
            housesAdapter.updateHouses(houses)
        } catch (e: Exception) {
            Toast.makeText(this, "Error parsing data: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
