package com.example.advogo.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.advogo.databinding.ItemProcessoHistoricoBinding
import com.example.advogo.databinding.ItemProcessoParteBinding
import com.example.advogo.models.ProcessoHistorico
import com.example.advogo.models.ProcessoParte
import java.text.SimpleDateFormat
import java.util.*

open class ProcessosPartesAdapter(
    private val context: Context,
    private var list: List<ProcessoParte>
): RecyclerView.Adapter<ProcessosPartesAdapter.MyViewHolder>() {
    private var onItemClickListener: OnItemClickListener? = null

    inner class MyViewHolder(private val binding: ItemProcessoParteBinding)
        : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: ProcessoParte, position: Int) {
            binding.apply {
                binding.tvNomeParte.text = item.nome
                binding.tvDocumentoCliente.text = "Documento: ${item.documento ?: "Não informado"}"
                binding.tvContatoParte.text = "Contato: ${item.contato ?: "Não informado"}"
                binding.tvTipoParte.text = "Tipo: ${item.tipoObj?.tipo ?: "Não informado"}"

                binding.root.setOnClickListener {
                    if (onItemClickListener != null) {
                        onItemClickListener!!.onClick(item, position)
                    }
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProcessosPartesAdapter.MyViewHolder {
        return MyViewHolder(
            ItemProcessoParteBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProcessosPartesAdapter.MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item, position)
    }

    override fun getItemCount(): Int = list.size

    interface OnItemClickListener {
        fun onClick(parte: ProcessoParte, position: Int)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }
}