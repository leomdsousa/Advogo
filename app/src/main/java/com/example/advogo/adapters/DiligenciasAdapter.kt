package com.example.advogo.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.advogo.R
import com.example.advogo.databinding.ItemDiligenciaBinding
import com.example.advogo.models.Cliente
import com.example.advogo.models.Diligencia
import java.text.SimpleDateFormat
import java.util.*

open class DiligenciasAdapter(
    private val context: Context,
    private var list: List<Diligencia>
): RecyclerView.Adapter<DiligenciasAdapter.MyViewHolder>() {
    private var onItemClickListener: OnItemClickListener? = null

    inner class MyViewHolder(private val binding: ItemDiligenciaBinding)
        : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: Diligencia, position: Int) {
            binding.apply {
                binding.tvDescricaoDiligencia.text = item.descricao
                binding.tvProcessoDiligencia.text = "Nº diligencia: ${item.processoObj?.numero}"
                binding.tvAdvDiligencia.text = "Advogado: ${item.advogadoObj?.nome} (${item.advogadoObj?.oab})"

                if(!item.data.isNullOrEmpty()) {
                    val fromFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                    val toFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
                    val fromDate = fromFormat.parse(item.data)
                    val data = toFormat.format(fromDate)
                    binding.tvDataDiligencia.text = "Data: $data"
                }

                binding.tvTipoDiligencia.text = "Tipo: ${item.tipoObj?.tipo}"
                binding.tvStatusDiligencia.text = "Status: ${item.statusObj?.status}"

                binding.root.setOnClickListener {
                    if (onItemClickListener != null) {
                        onItemClickListener!!.onClick(item, position)
                    }
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiligenciasAdapter.MyViewHolder {
        return MyViewHolder(
            ItemDiligenciaBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DiligenciasAdapter.MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item, position)
    }

    override fun getItemCount(): Int = list.size

    interface OnItemClickListener {
        fun onClick(diligencia: Diligencia, position: Int)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    fun updateList(newList: ArrayList<Diligencia>) {
        list = newList
        notifyDataSetChanged()
    }
}