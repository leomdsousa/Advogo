package com.example.advogo.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.advogo.R
import com.example.advogo.databinding.ItemDiligenciaBinding
import com.example.advogo.databinding.ItemDiligenciaHistoricoBinding
import com.example.advogo.models.DiligenciaHistorico
import java.text.SimpleDateFormat
import java.util.*

open class DiligenciasHistoricosAdapter(
    private val context: Context,
    private var list: List<DiligenciaHistorico>
): RecyclerView.Adapter<DiligenciasHistoricosAdapter.MyViewHolder>() {
    private var onItemClickListener: OnItemClickListener? = null

    inner class MyViewHolder(private val binding: ItemDiligenciaHistoricoBinding)
        : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: DiligenciaHistorico, position: Int) {
            binding.apply {
                binding.tvAdvDiligenciaHist.text = "Advogado: ${item.advogadoObj?.nome} (${item.advogadoObj?.oab})"

                if(!item.data.isNullOrEmpty()) {
                    val fromFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                    val toFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
                    val fromDate = fromFormat.parse(item.data)
                    val data = toFormat.format(fromDate)
                    binding.tvDataDiligenciaHist.text = "Data: $data"
                }

//                binding.tvTipoDiligenciaHist.text = "Tipo: ${item.tipoObj?.tipo}"
//                binding.tvStatusDiligenciaHist.text = "Status: ${item.statusObj?.status}"
                binding.tvObsDiligenciaHist.text = "Observação: ${item.obs}"

                binding.root.setOnClickListener {
                    if (onItemClickListener != null) {
                        onItemClickListener!!.onClick(item, position)
                    }
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiligenciasHistoricosAdapter.MyViewHolder {
        return MyViewHolder(
            ItemDiligenciaHistoricoBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DiligenciasHistoricosAdapter.MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item, position)
    }

    override fun getItemCount(): Int = list.size

    interface OnItemClickListener {
        fun onClick(diligencia: DiligenciaHistorico, position: Int)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }
}