package com.example.advogo.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.advogo.databinding.ItemDialogTipoParteBinding
import com.example.advogo.models.TiposPartes
import com.example.advogo.utils.constants.Constants

open class TiposPartesAdapter(
    private val context: Context,
    private var list: List<TiposPartes>
): RecyclerView.Adapter<TiposPartesAdapter.MyViewHolder>() {
    private var onItemClickListener: OnItemClickListener? = null

    inner class MyViewHolder(private val binding: ItemDialogTipoParteBinding)
        : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: TiposPartes, position: Int) {
            binding.apply {
                binding.tvDescricaoTipoParte .text = item.tipo

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TiposPartesAdapter.MyViewHolder {
        return MyViewHolder(
            ItemDialogTipoParteBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TiposPartesAdapter.MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item, position)
    }

    override fun getItemCount(): Int = list.size

    interface OnItemClickListener {
        fun onClick(tipoParte: TiposPartes, position: Int, action: String)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }
}