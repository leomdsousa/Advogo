package com.example.advogo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.advogo.databinding.ItemDiligenciaStatusBinding
import com.example.advogo.models.DiligenciaStatus
import com.example.advogo.utils.constants.Constants

open class DiligenciasStatusAdapter(
    private val context: Context,
    private val list: List<DiligenciaStatus>,
    private val readOnly: Boolean = true
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

                if(!readOnly) {
                    binding.btnEdit.visibility = View.VISIBLE
                    binding.btnDelete.visibility = View.VISIBLE

                    binding.btnEdit.setOnClickListener {
                        onItemClickListener!!.onEdit(item, position)
                    }

                    binding.btnDelete.setOnClickListener {
                        onItemClickListener!!.onDelete(item, position)
                    }
                } else {
                    binding.btnEdit.visibility = View.GONE
                    binding.btnDelete.visibility = View.GONE
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
        fun onClick(item: DiligenciaStatus, position: Int, action: String)
        fun onEdit(item: DiligenciaStatus, position: Int)
        fun onDelete(item: DiligenciaStatus, position: Int)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }
}