package com.example.advogo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.advogo.R
import com.example.advogo.databinding.ItemAdvogadoBinding
import com.example.advogo.models.Advogado
import com.example.advogo.utils.Constants
import com.example.projmgr.dialogs.AdvogadosDialog
import dagger.hilt.android.AndroidEntryPoint

open class AdvogadosAdapter(
    private val context: Context,
    private var list: ArrayList<Advogado>
): RecyclerView.Adapter<AdvogadosAdapter.MyViewHolder>() {
    private var onItemClickListener: OnItemClickListener? = null

    inner class MyViewHolder(private val binding: ItemAdvogadoBinding)
        : RecyclerView.ViewHolder(binding.root) {
            fun bind(item: Advogado, position: Int) {
                binding.apply {
                    Glide
                        .with(context)
                        .load(item.imagem)
                        .centerCrop()
                        .placeholder(R.drawable.ic_user_place_holder)
                        .into(binding.ivAdvImage)

                    binding.tvAdvName.text = item.nome
                    binding.tvAdvEmail.text = item.email

                    if (item.selecionado) {
                        binding.ivSelectedAdv.visibility = View.VISIBLE
                    } else {
                        binding.ivSelectedAdv.visibility = View.GONE
                    }

                    binding.root.setOnClickListener {
                        if(item.selecionado) {
                            onItemClickListener!!.onClick(position, item, Constants.DESELECIONAR)
                        } else {
                            onItemClickListener!!.onClick(position, item, Constants.SELECIONAR)
                        }
                    }
                }
            }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
                ItemAdvogadoBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
            )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item, position)
    }

    override fun getItemCount(): Int = list.size

    interface OnItemClickListener {
        fun onClick(position: Int, user: Advogado, action: String)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }
}