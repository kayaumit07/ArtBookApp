package com.example.artbookapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Build.VERSION
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.text.method.Touch
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.artbookapp.Singleton.detailsEditable
import com.example.artbookapp.databinding.ActivityArtBookDetailsBinding
import com.google.android.material.snackbar.Snackbar
import org.w3c.dom.Text
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.security.Permission

private lateinit var binding: ActivityArtBookDetailsBinding
private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
private lateinit var permissionLauncher: ActivityResultLauncher<String>
var selectedBitmap: Bitmap? = null
private lateinit var database : SQLiteDatabase
@Suppress("DEPRECATION")
class artBookDetails : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityArtBookDetailsBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        database = this.openOrCreateDatabase("artbooks", Context.MODE_PRIVATE,null)
        registerLauncher()

        binding.button3.setOnClickListener(){
            saveNewEntry(view)

        }

       chechEditable()


        val selectedArtBooks=Singleton.chosenArtBook
        selectedArtBooks?.let {
            binding.textArtName.setText(it.artName)
            binding.textArtistName.setText(it.artistName)
            binding.textYear.setText(it.year)
            println(it.id.toString())
              val bitmap = BitmapFactory.decodeByteArray(it.imageData,0,it.imageData.size)
             binding.imageView.setImageBitmap(bitmap)



        }

    }
    fun saveNewEntry(view: View){
       if(binding.textArtName.text.isEmpty()){
           val builder = AlertDialog.Builder(this)
           builder.setTitle("Alert")
           builder.setMessage("Do not empty texts")
           builder.show()
           return
       }
        if(binding.textArtistName.text.isEmpty()){
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Alert")
            builder.setMessage("Do not empty texts")
            builder.show()
            return
        }
        if(binding.textYear.text.isEmpty()){
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Alert")
            builder.setMessage("Do not empty texts")
            builder.show()
            return
        }

        val artName = binding.textArtName.text.toString()
        val artistName = binding.textArtistName.text.toString()
        val year = binding.textYear.text.toString()

        if (selectedBitmap != null) {
            val smallBitmap = makeSmallerBitmap(selectedBitmap!!,300)

            val outputStream = ByteArrayOutputStream()
            smallBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
            val byteArray = outputStream.toByteArray()

            try {

                database.execSQL("CREATE TABLE IF NOT EXISTS artbooks (id INTEGER PRIMARY KEY, artname VARCHAR, artistname VARCHAR, year VARCHAR, image BLOB)")

                val sqlString = "INSERT INTO artbooks (artname, artistname, year, image) VALUES (?, ?, ?, ?)"
                val statement = database.compileStatement(sqlString)
                statement.bindString(1, artName)
                statement.bindString(2, artistName)
                statement.bindString(3, year)
                statement.bindBlob(4, byteArray)

                statement.execute()

            } catch (e: Exception) {
                e.printStackTrace()
            }


            val intent = Intent(this,MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            startActivity(intent)

            //finish()
        }

    }

    override fun onResume() {
        super.onResume()
        if(Singleton.detailsEditable){
            binding.textArtName.setText("")
            binding.textArtistName.setText("")
            binding.textYear.setText("")
            binding.imageView.setImageResource(R.drawable.ic_launcher_background)
        }
    }


    fun selectImage(view: View){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES)) {
                    Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",
                        View.OnClickListener {
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                        }).show()
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            } else {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        } else {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",
                        View.OnClickListener {
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }).show()
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            } else {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }
    }
    private fun registerLauncher(){

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val intentFromResult = result.data
                if (intentFromResult != null) {
                    val imageData = intentFromResult.data
                    try {
                        if (Build.VERSION.SDK_INT >= 28) {
                            val source = ImageDecoder.createSource(this@artBookDetails.contentResolver, imageData!!)
                            selectedBitmap = ImageDecoder.decodeBitmap(source)
                            binding.imageView.setImageBitmap(selectedBitmap)
                        } else {
                            selectedBitmap = MediaStore.Images.Media.getBitmap(this@artBookDetails.contentResolver, imageData)
                            binding.imageView.setImageBitmap(selectedBitmap)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                //permission granted
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else {
                //permission denied
                Toast.makeText(this@artBookDetails, "Permisson needed!", Toast.LENGTH_LONG).show()
            }
        }

    }

    fun makeSmallerBitmap(image: Bitmap, maximumSize : Int) : Bitmap {
        var width = image.width
        var height = image.height

        val bitmapRatio : Double = width.toDouble() / height.toDouble()
        if (bitmapRatio > 1) {
            width = maximumSize
            val scaledHeight = width / bitmapRatio
            height = scaledHeight.toInt()
        } else {
            height = maximumSize
            val scaledWidth = height * bitmapRatio
            width = scaledWidth.toInt()
        }
        return Bitmap.createScaledBitmap(image,width,height,true)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun chechEditable(){
        if(!Singleton.detailsEditable) {
            binding.textArtName.focusable = View.NOT_FOCUSABLE
            binding.textArtName.inputType = InputType.TYPE_NULL
            binding.textYear.focusable = View.NOT_FOCUSABLE
            binding.textYear.inputType = InputType.TYPE_NULL
            binding.textArtistName.focusable = View.NOT_FOCUSABLE
            binding.textArtistName.inputType = InputType.TYPE_NULL
            binding.button3.visibility=View.INVISIBLE
            binding.imageView.focusable=View.NOT_FOCUSABLE
            binding.imageView.isClickable=false


        }
        else
        {
            binding.textArtName.focusable = View.FOCUSABLE
            binding.textArtName.inputType = InputType.TYPE_CLASS_TEXT
            binding.textYear.focusable = View.FOCUSABLE
            binding.textYear.inputType = InputType.TYPE_CLASS_DATETIME
            binding.textArtistName.focusable = View.FOCUSABLE
            binding.textArtistName.inputType = InputType.TYPE_CLASS_TEXT
            binding.button3.visibility=View.VISIBLE
            binding.imageView.focusable=View.FOCUSABLE
            binding.imageView.isClickable=true
            binding.imageView.setImageResource(R.drawable.ic_launcher_background)


        }
    }


}