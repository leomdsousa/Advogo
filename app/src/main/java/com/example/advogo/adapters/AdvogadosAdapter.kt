package com.example.advogo.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.advogo.R
import com.example.advogo.databinding.ItemAdvogadoBinding
import com.example.advogo.models.Advogado
import com.example.advogo.utils.constants.Constants

open class AdvogadosAdapter(
    private val context: Context,
    private val exibirIconeTelefone: Boolean = false,
    private val exibirIconeEmail: Boolean = false,
    private var list: ArrayList<Advogado>
): RecyclerView.Adapter<AdvogadosAdapter.MyViewHolder>() {
    private var onItemClickListener: OnItemClickListener? = null
    private val regexTelefone = Regex("(\\+55\\s?)?\\(\\d{2}\\)\\s?\\d{4,5}-\\d{4}")

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

                    binding.tvAdvName.text = "${item.nome} (${item.oab})"
                    binding.tvAdvEmail.text = item.email
                    binding.tvTelefoneAdv.text = item.telefone

                    if(exibirIconeTelefone
                        && (item.telefone != null && regexTelefone.matches(item.telefone!!))
                    ) {
                        binding.imageTelefone.visibility = View.VISIBLE
                        binding.imageTelefone.setOnClickListener { _ ->
                            val numeroTelefone = item.telefone!!
                            val uri: Uri = Uri.parse("tel:$numeroTelefone")
                            val intent = Intent(Intent.ACTION_DIAL, uri)
                            context.startActivity(intent)
                        }
                    } else {
                        binding.imageTelefone.visibility = View.GONE
                    }

                    if(exibirIconeEmail
                        && (item.email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(item.email!!).matches())
                    ) {
                        binding.imageEmail.visibility = View.VISIBLE
                        binding.imageEmail.setOnClickListener { _ ->
                            val enderecoEmail = item.email

                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:$enderecoEmail")
                            }

                            context.startActivity(intent)
                        }
                    } else {
                        binding.imageEmail.visibility = View.GONE
                    }

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
        fun onClick(position: Int, user: Advogado, action: String? = null)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    fun updateList(newList: ArrayList<Advogado>) {
        list = newList
        notifyDataSetChanged()
    }
}