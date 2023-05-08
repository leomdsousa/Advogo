package com.example.advogo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.advogo.R
import com.example.advogo.databinding.ItemProcessoBinding
import com.example.advogo.models.Processo

open class ProcessosAdapter(
    private val context: Context,
    private var list: ArrayList<Processo>
): RecyclerView.Adapter<ProcessosAdapter.MyViewHolder>() {
    private var onItemClickListener: OnItemClickListener? = null

    inner class MyViewHolder(private val binding: ItemProcessoBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Processo, position: Int) {
            binding.apply {
                Glide
                    .with(context)
                    .load(item.imagem)
                    .centerCrop()
                    .placeholder(R.drawable.ic_processo_place_holder)
                    .into(binding.ivProcessoImage)

                binding.tvDescricaoProcesso.text = item.descricao
                binding.tvNumeroProcesso.text = "Nº : ${item.numero}"
                binding.tvTipoProcesso.text = "Tipo: ${item.tipo}"
                binding.tvStatusProcesso.text = "Status: ${item.status}"

                binding.root.setOnClickListener {
                    if (onItemClickListener != null) {
                        onItemClickListener!!.onClick(item, position)
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
        fun onClick(processo: Processo, position: Int)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }
}