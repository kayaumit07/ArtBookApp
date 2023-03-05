/*
package com.example.artbookapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.artbookapp.databinding.ActivityCardFragmentBinding

class ArtBookAdapter(private val artList:ArrayList<ClassArtList>):RecyclerView.Adapter<ArtBookAdapter.ArtBookViewHolder>() {

    class ArtBookViewHolder(val binding:ActivityCardFragmentBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtBookViewHolder {
        val binding=ActivityCardFragmentBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ArtBookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArtBookViewHolder, position: Int) {
        holder.binding.textArtName.text=artList.get(position).artName
        holder.itemView.setOnClickListener{
            val intent= Intent(holder.itemView.context,artBookDetails::class.java)
            Singleton.chosenArtBook=artList.get(position)
            holder.itemView.context.startActivity(intent)
            Singleton.detailsEditable=false
        }
        holder.binding.textArtistName.text=artList.get(position).artistName
        holder.binding.textDate.text=artList.get(position).year
        holder.binding.imageViewArtTumb.setImageResource(R.drawable.ic_launcher_background)
    }

    override fun getItemCount(): Int {
        return artList.size
    }
}*/

package com.example.artbookapp

import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.example.artbookapp.databinding.ActivityCardFragmentBinding

class ArtBookAdapter(private val artList:ArrayList<ClassArtList>) : RecyclerView.Adapter<ArtBookAdapter.ArtBookHolder>() {

    class ArtBookHolder(val binding: ActivityCardFragmentBinding) : RecyclerView.ViewHolder(binding.root) {

    }
    //Holder ilk oluşturuldugunda ne olacak
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtBookHolder {
        val binding=ActivityCardFragmentBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ArtBookHolder(binding)
    }
    //Baglandıktan sonra ne olacak
    override fun onBindViewHolder(holder: ArtBookHolder, position: Int) {
        val byteArray=artList.get(position).imageData
        val bitmap= BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
        holder.binding.textArtName.text=artList.get(position).artName
        holder.binding.textArtistName .text=artList.get(position).artistName
        holder.binding.textDate.text=artList.get(position).year
        holder.binding.imageViewArtTumb.setImageBitmap(bitmap)

        holder.itemView.setOnClickListener {
            val intent= Intent(holder.itemView.context,artBookDetails::class.java)
            Singleton.chosenArtBook=artList.get(position)
            holder.itemView.context.startActivity(intent)
        }
        Singleton.detailsEditable=false

    }
    //kac tane olusturacagız bundan
    override fun getItemCount(): Int {
        return artList.size
    }

}

