package com.example.advogo.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.advogo.databinding.ItemProcessoHistoricoBinding
import com.example.advogo.models.ProcessoHistorico
import java.text.SimpleDateFormat
import java.util.*

open class ProcessosHistoricosAdapter(
    private val context: Context,
    private var list: List<ProcessoHistorico>
): RecyclerView.Adapter<ProcessosHistoricosAdapter.MyViewHolder>() {
    private var onItemClickListener: OnItemClickListener? = null

    inner class MyViewHolder(private val binding: ItemProcessoHistoricoBinding)
        : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: ProcessoHistorico, position: Int) {
            binding.apply {
                binding.tvAdvProcessoHist.text = "Advogado: ${item.advogadoObj?.nome} (${item.advogadoObj?.oab})"

                if(!item.data.isNullOrEmpty()) {
                    val fromFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                    val toFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
                    val fromDate = fromFormat.parse(item.data)
                    val data = toFormat.format(fromDate)
                    binding.tvDataProcessoHist.text = "Data: $data"
                }

//                binding.tvTipoProcessoHist.text = "Tipo: ${item.tipoObj?.tipo}"
//                binding.tvStatusProcessoHist.text = "Status: ${item.statusObj?.status}"
                binding.tvObsProcessoHist.text = "Observação: ${item.obs}"

                binding.root.setOnClickListener {
                    if (onItemClickListener != null) {
                        onItemClickListener!!.onClick(item, position)
                    }
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProcessosHistoricosAdapter.MyViewHolder {
        return MyViewHolder(
            ItemProcessoHistoricoBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProcessosHistoricosAdapter.MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item, position)
    }

    override fun getItemCount(): Int = list.size

    interface OnItemClickListener {
        fun onClick(processo: ProcessoHistorico, position: Int)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }
}