package com.example.advogo.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.advogo.databinding.ItemAnexoBinding
import com.example.advogo.models.Anexo

open class AnexosAdapter(
    private val context: Context,
    private var list: ArrayList<Anexo>,
): RecyclerView.Adapter<AnexosAdapter.MyViewHolder>() {
    private var onItemClickListener: OnItemClickListener? = null

    inner class MyViewHolder(private val binding: ItemAnexoBinding)
        : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: Anexo, position: Int) {
            binding.apply {
                binding.tvNomeAnexo.text = item.nome

                binding.btnAbrirAnexo.setOnClickListener {
                    if(!TextUtils.isEmpty(item.nome)) {
                        onItemClickListener!!.onView(item, position)
                    }
                }

                binding.btnRemoverAnexo.setOnClickListener {
                    onItemClickListener!!.onDelete(item, position)
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnexosAdapter.MyViewHolder {
        return MyViewHolder(
                ItemAnexoBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AnexosAdapter.MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item, position)
    }

    override fun getItemCount(): Int = list.size

    interface OnItemClickListener {
        fun onView(Anexo: Anexo, position: Int)
        fun onDelete(Anexo: Anexo, position: Int)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }
}