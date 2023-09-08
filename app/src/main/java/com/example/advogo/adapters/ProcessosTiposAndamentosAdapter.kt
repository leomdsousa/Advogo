package com.example.advogo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.advogo.databinding.ItemProcessoAndamentoTipoBinding
import com.example.advogo.models.ProcessoTipoAndamento
import com.example.advogo.utils.Constants

open class ProcessosTiposAndamentosAdapter(
    private val context: Context,
    private val list: List<ProcessoTipoAndamento>
) : RecyclerView.Adapter<ProcessosTiposAndamentosAdapter.MyViewHolder>() {
    private var onItemClickListener: OnItemClickListener? = null

    inner class MyViewHolder(private val binding: ItemProcessoAndamentoTipoBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProcessoTipoAndamento, position: Int) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProcessosTiposAndamentosAdapter.MyViewHolder {
        return MyViewHolder(
            ItemProcessoAndamentoTipoBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProcessosTiposAndamentosAdapter.MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item, position)
    }

    override fun getItemCount(): Int = list.size

    interface OnItemClickListener {
        fun onClick(processo: ProcessoTipoAndamento, position: Int, action: String)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }
}