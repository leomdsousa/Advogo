package com.example.advogo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.advogo.databinding.ItemDiligenciaTipoBinding
import com.example.advogo.models.DiligenciaTipo
import com.example.advogo.utils.constants.Constants

open class DiligenciasTiposAdapter(
    private val context: Context,
    private val list: List<DiligenciaTipo>
) : RecyclerView.Adapter<DiligenciasTiposAdapter.MyViewHolder>() {
    private var onItemClickListener: OnItemClickListener? = null

    inner class MyViewHolder(private val binding: ItemDiligenciaTipoBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DiligenciaTipo, position: Int) {
            binding.apply {
                binding.tvTipoDiligencia.text = item.tipo ?: "Sem Título"

                binding.root.setOnClickListener {
                    if(item.selecionado) {
                        onItemClickListener!!.onClick(item, position, Constants.DESELECIONAR)
                    } else {
                        onItemClickListener!!.onClick(item, position, Constants.SELECIONAR)
                    }
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiligenciasTiposAdapter.MyViewHolder {
        return MyViewHolder(
            ItemDiligenciaTipoBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DiligenciasTiposAdapter.MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item, position)
    }

    override fun getItemCount(): Int = list.size

    interface OnItemClickListener {
        fun onClick(item: DiligenciaTipo, position: Int, action: String)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }
}