package com.example.advogo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.advogo.databinding.ItemDiligenciaStatusBinding
import com.example.advogo.databinding.ItemProcessoStatusBinding
import com.example.advogo.models.DiligenciaStatus
import com.example.advogo.models.ProcessoStatus
import com.example.advogo.utils.Constants

open class DiligenciasStatusAdapter(
    private val context: Context,
    private val list: List<DiligenciaStatus>
) : RecyclerView.Adapter<DiligenciasStatusAdapter.MyViewHolder>() {
    private var onItemClickListener: OnItemClickListener? = null

    inner class MyViewHolder(private val binding: ItemDiligenciaStatusBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DiligenciaStatus, position: Int) {
            binding.apply {
                binding.tvStatusDiligencia.text = item.status ?: "Sem Título"

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiligenciasStatusAdapter.MyViewHolder {
        return MyViewHolder(
            ItemDiligenciaStatusBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DiligenciasStatusAdapter.MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item, position)
    }

    override fun getItemCount(): Int = list.size

    interface OnItemClickListener {
        fun onClick(processo: DiligenciaStatus, position: Int, action: String)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }
}