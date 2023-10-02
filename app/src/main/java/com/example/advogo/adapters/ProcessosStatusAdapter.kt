package com.example.advogo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.advogo.databinding.*
import com.example.advogo.models.ProcessoStatus
import com.example.advogo.utils.constants.Constants
import com.example.advogo.utils.enums.UseAdapterBindingFor

open class ProcessosStatusAdapter(
    private val context: Context,
    private val list: List<ProcessoStatus>,
    private val useIn: UseAdapterBindingFor
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onItemClickListener: OnItemClickListener? = null

    inner class ActivityOrFragmentViewHolder(private val binding: ItemProcessoStatusBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProcessoStatus, position: Int) {
            binding.apply {
                binding.tvDescricaoStatusProcesso.text = item.status ?: "Sem Título"

                binding.root.setOnClickListener {
                    if(item.selecionado == true) {
                        onItemClickListener!!.onClick(item, position, Constants.DESELECIONAR)
                    } else {
                        onItemClickListener!!.onClick(item, position, Constants.SELECIONAR)
                    }
                }

                binding.btnEdit.visibility = View.VISIBLE
                binding.btnDelete.visibility = View.VISIBLE

                binding.btnEdit.setOnClickListener {
                    onItemClickListener!!.onEdit(item, position)
                }

                binding.btnDelete.setOnClickListener {
                    onItemClickListener!!.onDelete(item, position)
                }
            }
        }
    }

    inner class DialogViewHolder(
        private val binding: ItemDialogProcessoStatusBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProcessoStatus, position: Int) {
            binding.tvDescricaoStatusProcesso.text = item.status ?: "Sem Título"

            binding.root.setOnClickListener {
                if(item.selecionado == true) {
                    onItemClickListener!!.onClick(item, position, Constants.DESELECIONAR)
                } else {
                    onItemClickListener!!.onClick(item, position, Constants.SELECIONAR)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        return when (useIn) {
            UseAdapterBindingFor.DIALOG -> {
                val dialogBinding = ItemDialogProcessoStatusBinding.inflate(inflater, parent, false)
                DialogViewHolder(dialogBinding)
            }
            UseAdapterBindingFor.ACTIVITY_OR_FRAGMENT -> {
                val binding = ItemProcessoStatusBinding.inflate(inflater, parent, false)
                ActivityOrFragmentViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        when (holder) {
            is ProcessosStatusAdapter.ActivityOrFragmentViewHolder -> holder.bind(item, position)
            is ProcessosStatusAdapter.DialogViewHolder -> holder.bind(item, position)
        }
    }

    override fun getItemCount(): Int = list.size

    interface OnItemClickListener {
        fun onClick(item: ProcessoStatus, position: Int, action: String)
        fun onEdit(item: ProcessoStatus, position: Int)
        fun onDelete(item: ProcessoStatus, position: Int)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }
}