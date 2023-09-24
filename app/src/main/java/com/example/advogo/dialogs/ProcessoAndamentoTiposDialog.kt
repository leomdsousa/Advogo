package com.example.advogo.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.advogo.adapters.ProcessosStatusAndamentosAdapter
import com.example.advogo.adapters.ProcessosTiposAndamentosAdapter
import com.example.advogo.databinding.DialogListBinding
import com.example.advogo.models.ProcessoStatusAndamento
import com.example.advogo.models.ProcessoTipoAndamento
import com.example.advogo.utils.enums.UseAdapterBindingFor

abstract class ProcessoAndamentoTiposDialog(
    context: Context,
    private val list: List<ProcessoTipoAndamento>
): Dialog(context) {
    private lateinit var binding: DialogListBinding
    private var adapter: ProcessosTiposAndamentosAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState ?: Bundle())

        binding = DialogListBinding.inflate(layoutInflater)
        binding.tvTitle.text = "Selecione o tipo do Andamento"

        setContentView(binding.root)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setupRecyclerView(binding.root)
    }

    private fun setupRecyclerView(view: View) {
        if (list.isNotEmpty()) {
            binding.rvList.layoutManager = LinearLayoutManager(context)
            adapter = ProcessosTiposAndamentosAdapter(context, list, UseAdapterBindingFor.DIALOG)
            binding.rvList.adapter = adapter

            adapter!!.setOnItemClickListener(object :
                ProcessosTiposAndamentosAdapter.OnItemClickListener {
                override fun onClick(item: ProcessoTipoAndamento, position: Int, action: String) {
                    dismiss()
                    onItemSelected(item, action)
                }
                override fun onEdit(item: ProcessoTipoAndamento, position: Int) {
                    return
                }
                override fun onDelete(item: ProcessoTipoAndamento, position: Int) {
                    return
                }
            })
        }
    }

    protected abstract fun onItemSelected(item: ProcessoTipoAndamento, action:String)
}