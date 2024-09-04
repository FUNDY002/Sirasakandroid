package com.example.sirasakandroid

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.sirasakandroid.MainActivity
import com.example.sirasakandroid.R
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream

class Adddata : AppCompatActivity() {

    private lateinit var editAreaSize: EditText
    private lateinit var editBedroom: EditText
    private lateinit var editBathrooms: EditText
    private lateinit var editPrice: EditText
    private lateinit var editCondition: EditText
    private lateinit var editHouseType: EditText
    private lateinit var editYearBuilt: EditText
    private lateinit var editParkingSpaces: EditText
    private lateinit var editAddress: EditText
    private lateinit var saveButton: Button
    private lateinit var backButton: Button
    private lateinit var selectImageButton: Button
    private lateinit var imageView: ImageView
    private lateinit var uploadButton: Button

    private val client = OkHttpClient()
    private val gson = Gson()

    private var selectedImageUri: Uri? = null

    private val selectImageLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                selectedImageUri = data?.data
                updateImageView()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adddata)

        // Initialize views
        editAreaSize = findViewById(R.id.EditAreaSize)
        editBedroom = findViewById(R.id.EditBedroom)
        editBathrooms = findViewById(R.id.EditBathrooms)
        editPrice = findViewById(R.id.EditPrice)
        editCondition = findViewById(R.id.EditConditionn)
        editHouseType = findViewById(R.id.EditHouseType)
        editYearBuilt = findViewById(R.id.EditYearBuilt)
        editParkingSpaces = findViewById(R.id.EditParkingSpaces)
        editAddress = findViewById(R.id.EditAddress)
        saveButton = findViewById(R.id.btn_save)
        backButton = findViewById(R.id.btn_back)
        imageView = findViewById(R.id.imageView)
        uploadButton = findViewById(R.id.btn_upload)

        backButton.setOnClickListener {
            finish()
        }

        saveButton.setOnClickListener {
            saveHouse()
        }
        uploadButton.setOnClickListener {
            selectImage()
        }

    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        selectImageLauncher.launch(intent)
    }

    private fun updateImageView() {
        selectedImageUri?.let { uri ->
            try {
                val inputStream: InputStream? = contentResolver.openInputStream(uri)
                val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
                imageView.setImageBitmap(bitmap)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    private fun saveHouse() {
        val areaSize = editAreaSize.text.toString().trim()
        val bedroom = editBedroom.text.toString().toIntOrNull() ?: 0
        val bathrooms = editBathrooms.text.toString().toIntOrNull() ?: 0
        val price = editPrice.text.toString().toDoubleOrNull() ?: 0.0
        val condition = editCondition.text.toString().trim()
        val houseType = editHouseType.text.toString().trim()
        val yearBuilt = editYearBuilt.text.toString().toIntOrNull() ?: 0
        val parkingSpaces = editParkingSpaces.text.toString().toIntOrNull() ?: 0
        val address = editAddress.text.toString().trim()

        val file = selectedImageUri?.let { uri ->
            val inputStream = contentResolver.openInputStream(uri)
            val file = File(cacheDir, "image.jpg")
            file.outputStream().use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
            file
        }

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("AreaSize", areaSize)
            .addFormDataPart("Bedrooms", bedroom.toString())
            .addFormDataPart("Bathrooms", bathrooms.toString())
            .addFormDataPart("Price", price.toString())
            .addFormDataPart("Conditionn", condition)
            .addFormDataPart("HouseType", houseType)
            .addFormDataPart("YearBuilt", yearBuilt.toString())
            .addFormDataPart("ParkingSpaces", parkingSpaces.toString())
            .addFormDataPart("Address", address)
            .apply {
                file?.let {
                    addFormDataPart(
                        "HouseImage", // Updated field name
                        it.name,
                        it.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    )
                }
            }
            .build()

        if (file == null&& selectedImageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            return
        }
        if(areaSize.isEmpty()||bedroom==null||bathrooms==null||price==null||condition.isEmpty()||houseType.isEmpty()||yearBuilt==null||parkingSpaces==null||address.isEmpty()){
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val request = Request.Builder()
            .url("http://10.13.4.108:3000/add/houses")
            .post(requestBody)
            .build()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = client.newCall(request).execute()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@Adddata,
                            "House added successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@Adddata,
                            "Server Error: ${response.code}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@Adddata,
                        "Error Adding House: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
