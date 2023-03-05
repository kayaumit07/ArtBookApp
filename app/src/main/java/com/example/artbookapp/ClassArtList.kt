package com.example.artbookapp

import android.graphics.Bitmap
import java.io.Serializable


class ClassArtList(
    val id: Int,
    val artName:String,
    val artistName:String,
    val year: String,
    val imageData: ByteArray
): Serializable {

}