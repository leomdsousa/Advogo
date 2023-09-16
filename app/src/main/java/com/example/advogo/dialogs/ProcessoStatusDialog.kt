package com.example.advogo.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.advogo.adapters.ProcessosStatusAdapter
import com.example.advogo.adapters.ProcessosTiposAdapter
import com.example.advogo.databinding.DialogListBinding
import com.example.advogo.models.ProcessoStatus
import com.example.advogo.models.ProcessoTipo

abstract class ProcessoStatusDialog(
    context: Context,
    private val list: List<ProcessoStatus>
): Dialog(context) {
    private lateinit var binding: DialogListBinding
    private var adapter: ProcessosStatusAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState ?: Bundle())

        binding = DialogListBinding.inflate(layoutInflater)
        binding.tvTitle.text = "Selecione o status do Processo"

        setContentView(binding.root)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setupRecyclerView(binding.root)
    }

    private fun setupRecyclerView(view: View) {
        if (list.isNotEmpty()) {
            binding.rvList.layoutManager = LinearLayoutManager(context)
            adapter = ProcessosStatusAdapter(context, list)
            binding.rvList.adapter = adapter

            adapter!!.setOnItemClickListener(object :
                ProcessosStatusAdapter.OnItemClickListener {
                override fun onClick(item: ProcessoStatus, position: Int, action: String) {
                    dismiss()
                    onItemSelected(item, action)
                }
                override fun onEdit(item: ProcessoStatus, position: Int) {
                    return;
                }

                override fun onDelete(item: ProcessoStatus, position: Int) {
                    return;
                }
            })
        }
    }

    protected abstract fun onItemSelected(item: ProcessoStatus, action:String)
}