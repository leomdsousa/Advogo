package com.example.advogo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.advogo.R
import com.example.advogo.databinding.ItemClienteBinding
import com.example.advogo.databinding.ItemProcessoBinding
import com.example.advogo.models.Cliente
import com.example.advogo.models.Processo

open class ClientesAdapter(
    private val context: Context,
    private var list: ArrayList<Cliente>
): RecyclerView.Adapter<ClientesAdapter.MyViewHolder>() {
    private var onItemClickListener: OnItemClickListener? = null

    inner class MyViewHolder(private val binding: ItemClienteBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Cliente, position: Int) {
            binding.apply {
                binding.tvNomeCliente.text = item.nome
                binding.tvTelefoneCliente.text = "Telefone: ${item.telefone}"
                binding.tvEmailCliente.text = "Email: ${item.email}"
                binding.tvEnderecoCliente.text = "Endereço: ${item.endereco}"

                binding.root.setOnClickListener {
                    if (onItemClickListener != null) {
                        onItemClickListener!!.onClick(item, position)
                    }
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientesAdapter.MyViewHolder {
        return MyViewHolder(
                ItemClienteBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ClientesAdapter.MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item, position)
    }

    override fun getItemCount(): Int = list.size

    interface OnItemClickListener {
        fun onClick(cliente: Cliente, position: Int)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }
}