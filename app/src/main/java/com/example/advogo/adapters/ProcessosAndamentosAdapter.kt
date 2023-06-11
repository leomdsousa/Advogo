package com.example.advogo.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.advogo.R
import com.example.advogo.databinding.ItemAndamentoBinding
import com.example.advogo.databinding.ItemDiligenciaBinding
import com.example.advogo.models.Diligencia
import com.example.advogo.models.ProcessoAndamento
import java.text.SimpleDateFormat
import java.util.*

open class ProcessosAndamentosAdapter(
    private val context: Context,
    private var list: List<ProcessoAndamento>
): RecyclerView.Adapter<ProcessosAndamentosAdapter.MyViewHolder>() {
    private var onItemClickListener: OnItemClickListener? = null

    inner class MyViewHolder(private val binding: ItemAndamentoBinding)
        : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: ProcessoAndamento, position: Int) {
            binding.apply {
                binding.tvTipoAndamento.text = "Tipo: ${item.tipoObj?.tipo}"
                binding.tvAdvAndamento.text = "Advogado: ${item.advogadoObj?.nome} (${item.advogadoObj?.oab})"

                if(!item.data.isNullOrEmpty()) {
                    val fromFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                    val toFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
                    val fromDate = fromFormat.parse(item.data)
                    val data = toFormat.format(fromDate)
                    binding.tvDataAndamento.text = "Data: $data"
                }

                binding.tvDescricaoAndamento.text = "Descrição: ${item.descricao}"

                binding.root.setOnClickListener {
                    if (onItemClickListener != null) {
                        onItemClickListener!!.onClick(item, position)
                    }
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProcessosAndamentosAdapter.MyViewHolder {
        return MyViewHolder(
            ItemAndamentoBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProcessosAndamentosAdapter.MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item, position)
    }

    override fun getItemCount(): Int = list.size

    interface OnItemClickListener {
        fun onClick(andamento: ProcessoAndamento, position: Int)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }
}