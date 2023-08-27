package com.example.advogo.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.advogo.databinding.ItemAnexoBinding
import com.example.advogo.models.Anexo
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

open class AnexosAdapter(
    private val context: Context,
    private var list: List<Anexo>?,
): RecyclerView.Adapter<AnexosAdapter.MyViewHolder>() {
    private var onItemClickListener: OnItemClickListener? = null

    inner class MyViewHolder(private val binding: ItemAnexoBinding)
        : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: Anexo, position: Int) {
            binding.apply {
                binding.tvNomeAnexo.text = item.nome
                //binding.tvUsuarioAnexo.text = item.advogadoObj?.nome

                if(!item.data.isNullOrEmpty()) {
                    val fromFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                    val toFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
                    val fromDate = fromFormat.parse(item.data)
                    val data = toFormat.format(fromDate)
                    binding.tvDataUsuarioAnexo.text =
                        "$data - ${item.advogadoObj?.nome} (${item.advogadoObj?.oab})"
                }

                binding.btnAbrirAnexo.setOnClickListener {
                    if(!TextUtils.isEmpty(item.nome)) {
                        onItemClickListener!!.onView(item, position)
                    }
                }

                binding.btnRemoverAnexo.setOnClickListener {
                    onItemClickListener!!.onDelete(item, position)
                }

                binding.root.setOnClickListener {
                    onItemClickListener!!.onClick(item)
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
        val item = list?.get(position)

        if(item != null) {
            holder.bind(item, position)
        }
    }

    override fun getItemCount(): Int = list?.size ?: 0

    interface OnItemClickListener {
        fun onClick(Anexo: Anexo)
        fun onView(Anexo: Anexo, position: Int)
        fun onDelete(Anexo: Anexo, position: Int)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }
}