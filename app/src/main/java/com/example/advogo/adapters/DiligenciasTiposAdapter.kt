package com.example.advogo.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.advogo.models.DiligenciaTipo

open class DiligenciasTiposAdapter(
    context: Context,
    private val lista: List<DiligenciaTipo>
) : ArrayAdapter<DiligenciaTipo>(context, android.R.layout.simple_spinner_item, lista) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val diligenciaTipo = getItem(position)
        diligenciaTipo?.let {
            val descricaoTextView = view.findViewById<TextView>(android.R.id.text1)
            descricaoTextView.text = it.tipo
        }
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val diligenciaTipo = getItem(position)
        diligenciaTipo?.let {
            val descricaoTextView = view.findViewById<TextView>(android.R.id.text1)
            descricaoTextView.text = it.tipo
        }
        return view
    }
}