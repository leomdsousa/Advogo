package com.example.advogo.adapters

import android.content.DialogInterface
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.advogo.R

class OptionsAdapter(
    private val options: Array<String>,
    private val onOptionSelected: (String) -> Unit,
    private val optionSelected: Int? = null
    ) : RecyclerView.Adapter<OptionsAdapter.OptionViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_option, parent, false)
        return OptionViewHolder(view)
    }

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        val option = options[position]
        holder.bind(option, position)
    }

    override fun getItemCount(): Int {
        return options.size
    }

    inner class OptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(android.R.id.text1)

        fun bind(option: String, position: Int) {
            textView.text = option

            if (position == optionSelected && !arrayOf("Limpar").contains(option)) {
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.secondaryTextColor))
                textView.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
            } else {
                itemView.setBackgroundColor(Color.TRANSPARENT)
            }

            itemView.setOnClickListener {
                //(itemView.context as? DialogInterface)?.dismiss()
                onOptionSelected(option)
            }
        }
    }
}
