package com.tugas.suratjalan

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BarangViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.item_barang, parent, false)) {
    private var textNo: TextView? = null
    private var textNama: TextView? = null
    private var textBanyak: TextView? = null
    private var textKet: TextView? = null

    init {
        textNo = itemView.findViewById(R.id.textNo)
        textNama = itemView.findViewById(R.id.textNama)
        textBanyak = itemView.findViewById(R.id.textBanyak)
        textKet = itemView.findViewById(R.id.textKet)
    }

    fun bind(barang: Barang) {
        textNo?.text = barang.no.toString()
        textNama?.text = barang.nama
        textBanyak?.text = barang.banyak.toString()
        textKet?.text = barang.ket

        if (barang.no==-1) textNo?.text = "No"
        if (barang.banyak==-1) textBanyak?.text = "Banyak"
    }
}