package com.tugas.suratjalan

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.view.*

class BarangAdapter(private val list: List<Barang>) : RecyclerView.Adapter<BarangViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarangViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return BarangViewHolder(inflater,parent)
    }

    override fun onBindViewHolder(holder: BarangViewHolder, position: Int) {
        val barang: Barang = list[position]
        holder.bind(barang)
    }

    override fun getItemCount(): Int = list.size
}