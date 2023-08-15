package com.example.advogo.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.advogo.R
import com.example.advogo.databinding.ItemProcessoBinding
import com.example.advogo.models.Processo
import com.example.advogo.utils.Constants

open class ProcessosAdapter(
    private val context: Context,
    private var list: List<Processo>
): RecyclerView.Adapter<ProcessosAdapter.MyViewHolder>() {
    private var onItemClickListener: OnItemClickListener? = null

    inner class MyViewHolder(private val binding: ItemProcessoBinding)
        : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: Processo, position: Int) {
            binding.apply {
                Glide
                    .with(context)
                    .load(item.imagem)
                    .centerCrop()
                    .placeholder(R.drawable.image_placeholder)
                    .into(binding.ivProcessoImage)

                binding.tvDescricaoProcesso.text = item.titulo ?: "Sem Título"
                binding.tvNumeroProcesso.text = "Nº Processo: ${item.numero}"
                binding.tvTipoProcesso.text = "Tipo: ${item.tipoObj?.tipo}"
                binding.tvStatusProcesso.text = "Status: ${item.statusObj?.status}"

                if (item.selecionado) {
                    binding.ivSelected.visibility = View.VISIBLE
                } else {
                    binding.ivSelected.visibility = View.GONE
                }

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProcessosAdapter.MyViewHolder {
        return MyViewHolder(
                ItemProcessoBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProcessosAdapter.MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item, position)
    }

    override fun getItemCount(): Int = list.size

    interface OnItemClickListener {
        fun onClick(processo: Processo, position: Int, action: String)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    fun updateList(newList: ArrayList<Processo>) {
        list = newList
        notifyDataSetChanged()
    }
}