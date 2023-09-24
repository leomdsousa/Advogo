package com.example.advogo.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.advogo.databinding.ItemDialogProcessoAndamentoTipoBinding
import com.example.advogo.databinding.ItemProcessoAndamentoTipoBinding
import com.example.advogo.models.ProcessoTipoAndamento
import com.example.advogo.utils.constants.Constants
import com.example.advogo.utils.enums.UseAdapterBindingFor

open class ProcessosTiposAndamentosAdapter(
    private val context: Context,
    private val list: List<ProcessoTipoAndamento>,
    private val useIn: UseAdapterBindingFor
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onItemClickListener: OnItemClickListener? = null

    inner class ActivityOrFragmentViewHolder(private val binding: ItemProcessoAndamentoTipoBinding)
        : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: ProcessoTipoAndamento, position: Int) {
            binding.apply {
                binding.tvDescricaoTipoAndamento.text = item.tipo ?: "Sem Título"
                binding.tvPrazoTipoAndamento.text = "Prazo: ${item.prazo ?: "Não definido"}"

                if(item.prazo != null && item.prazo!! > 0) {
                    binding.tvSomenteDiasUteisTipoAndamento.visibility = View.VISIBLE
                    binding.tvSomenteDiasUteisTipoAndamento.text =
                        "Somente dias úteis: ${if (item.somenteDiaUtil == true) "Sim" else "Não"}"
                } else {
                    binding.tvSomenteDiasUteisTipoAndamento.visibility = View.GONE
                }

                binding.root.setOnClickListener {
                    if(item.selecionado) {
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
        private val binding: ItemDialogProcessoAndamentoTipoBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: ProcessoTipoAndamento, position: Int) {
            binding.tvDescricaoTipoProcesso.text = item.tipo ?: "Sem Título"

            binding.tvPrazoTipoProcesso.text = "Prazo: ${item.prazo ?: "Não definido"}"

            if(item.prazo != null && item.prazo!! > 0) {
                binding.tvSomenteDiasUteisTipoProcesso.visibility = View.VISIBLE
                binding.tvSomenteDiasUteisTipoProcesso.text =
                    "Somente dias úteis: ${if (item.somenteDiaUtil == true) "Sim" else "Não"}"
            } else {
                binding.tvSomenteDiasUteisTipoProcesso.visibility = View.GONE
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        return when (useIn) {
            UseAdapterBindingFor.DIALOG -> {
                val dialogBinding = ItemDialogProcessoAndamentoTipoBinding.inflate(inflater, parent, false)
                DialogViewHolder(dialogBinding)
            }
            UseAdapterBindingFor.ACTIVITY_OR_FRAGMENT -> {
                val binding = ItemProcessoAndamentoTipoBinding.inflate(inflater, parent, false)
                ActivityOrFragmentViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        when (holder) {
            is ActivityOrFragmentViewHolder -> holder.bind(item, position)
            is DialogViewHolder -> holder.bind(item, position)
        }
    }

    override fun getItemCount(): Int = list.size

    interface OnItemClickListener {
        fun onClick(item: ProcessoTipoAndamento, position: Int, action: String)
        fun onEdit(item: ProcessoTipoAndamento, position: Int)
        fun onDelete(item: ProcessoTipoAndamento, position: Int)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }
}