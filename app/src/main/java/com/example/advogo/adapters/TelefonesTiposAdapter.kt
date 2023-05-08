package com.example.advogo.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.advogo.models.TelefoneTipo

open class TelefonesTiposAdapter(
    context: Context,
    private val lista: List<TelefoneTipo>
) : ArrayAdapter<TelefoneTipo>(context, android.R.layout.simple_spinner_item, lista) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val telefoneTipo = getItem(position)
        telefoneTipo?.let {
            val descricaoTextView = view.findViewById<TextView>(android.R.id.text1)
            descricaoTextView.text = it.tipo
        }
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val telefoneTipo = getItem(position)
        telefoneTipo?.let {
            val descricaoTextView = view.findViewById<TextView>(android.R.id.text1)
            descricaoTextView.text = it.tipo
        }
        return view
    }
}