package com.example.advogo.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.advogo.adapters.ProcessosStatusAndamentosAdapter
import com.example.advogo.databinding.DialogListBinding
import com.example.advogo.models.ProcessoStatusAndamento

abstract class ProcessoAndamentoStatusDialog(
    context: Context,
    private val list: List<ProcessoStatusAndamento>
): Dialog(context) {
    private lateinit var binding: DialogListBinding
    private var adapter: ProcessosStatusAndamentosAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState ?: Bundle())

        binding = DialogListBinding.inflate(layoutInflater)
        binding.tvTitle.text = "Selecione o status do Andamento"

        setContentView(binding.root)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setupRecyclerView(binding.root)
    }

    private fun setupRecyclerView(view: View) {
        if (list.isNotEmpty()) {
            binding.rvList.layoutManager = LinearLayoutManager(context)
            adapter = ProcessosStatusAndamentosAdapter(context, list)
            binding.rvList.adapter = adapter

            adapter!!.setOnItemClickListener(object :
                ProcessosStatusAndamentosAdapter.OnItemClickListener {
                override fun onClick(item: ProcessoStatusAndamento, position: Int, action: String) {
                    dismiss()
                    onItemSelected(item, action)
                }
                override fun onEdit(item: ProcessoStatusAndamento, position: Int) {
                    return
                }
                override fun onDelete(item: ProcessoStatusAndamento, position: Int) {
                    return
                }
            })
        }
    }

    protected abstract fun onItemSelected(item: ProcessoStatusAndamento, action:String)
}