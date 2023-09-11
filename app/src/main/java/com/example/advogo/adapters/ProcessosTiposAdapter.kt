package com.example.advogo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.advogo.databinding.ItemProcessoTipoBinding
import com.example.advogo.models.ProcessoTipo
import com.example.advogo.utils.constants.Constants

open class ProcessosTiposAdapter(
    private val context: Context,
    private val list: List<ProcessoTipo>
) : RecyclerView.Adapter<ProcessosTiposAdapter.MyViewHolder>() {
    private var onItemClickListener: OnItemClickListener? = null

    inner class MyViewHolder(private val binding: ItemProcessoTipoBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProcessoTipo, position: Int) {
            binding.apply {
                binding.tvDescricaoTipoProcesso.text = item.tipo ?: "Sem Título"

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProcessosTiposAdapter.MyViewHolder {
        return MyViewHolder(
            ItemProcessoTipoBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProcessosTiposAdapter.MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item, position)
    }

    override fun getItemCount(): Int = list.size

    interface OnItemClickListener {
        fun onClick(processo: ProcessoTipo, position: Int, action: String)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }
}