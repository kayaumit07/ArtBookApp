package com.example.artbookapp

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore.Images.Media.getBitmap
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.artbookapp.databinding.ActivityMainBinding
import java.io.ByteArrayOutputStream


private lateinit var binding: ActivityMainBinding
private lateinit var artList:ArrayList<ClassArtList>

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        artList= ArrayList<ClassArtList>()
        try
        {
            val myDataBase=this.openOrCreateDatabase("artbooks", MODE_PRIVATE,null)
            myDataBase.execSQL("CREATE TABLE IF NOT EXISTS artbooks (id INTEGER PRIMARY KEY, artname VARCHAR, artistname VARCHAR, year VARCHAR, image BLOB)")
            val cursor=myDataBase.rawQuery("SELECT * FROM artbooks",null)
            val idIndex=cursor.getColumnIndex("id")
            val artnameIndex=cursor.getColumnIndex("artname")
            val artistnameIndex=cursor.getColumnIndex("artistname")
            val yearIndex=cursor.getColumnIndex("year")
            val imagedataIndex=cursor.getColumnIndex("image")






            var sayac:Int=0;
            while (cursor.moveToNext())
            {
//                val it=

                println("ID: ${cursor.getInt(idIndex)} artName:${cursor.getString(artnameIndex)} ")
                artList.add(ClassArtList(cursor.getInt(idIndex),cursor.getString(artnameIndex),
                    cursor.getString(artistnameIndex),cursor.getString(yearIndex),cursor.getBlob(imagedataIndex)))
                sayac++
                println(sayac.toString())
            }
            cursor.close()


        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }



        val artBookAdapter=ArtBookAdapter(artList)
        binding.rwArtList.layoutManager = LinearLayoutManager(this)
        binding.rwArtList.adapter = artBookAdapter

        println("dm")


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //inflater
        val menuInflater=getMenuInflater()
        menuInflater.inflate(R.menu.art_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==R.id.add_art_card)
        {
            val intent= Intent(this,artBookDetails::class.java)
            Singleton.detailsEditable=true
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }



}
