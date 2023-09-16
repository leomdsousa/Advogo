package com.example.advogo.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.advogo.adapters.DiligenciasTiposAdapter
import com.example.advogo.adapters.ProcessosTiposAdapter
import com.example.advogo.databinding.DialogListBinding
import com.example.advogo.models.DiligenciaTipo
import com.example.advogo.models.ProcessoTipo

abstract class DiligenciaTiposDialog(
    context: Context,
    private val list: List<DiligenciaTipo>
): Dialog(context) {
    private lateinit var binding: DialogListBinding
    private var adapter: DiligenciasTiposAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState ?: Bundle())

        binding = DialogListBinding.inflate(layoutInflater)
        binding.tvTitle.text = "Selecione o tipo de Diligencia"

        setContentView(binding.root)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setupRecyclerView(binding.root)
    }

    private fun setupRecyclerView(view: View) {
        if (list.isNotEmpty()) {
            binding.rvList.layoutManager = LinearLayoutManager(context)
            adapter = DiligenciasTiposAdapter(context, list)
            binding.rvList.adapter = adapter

            adapter!!.setOnItemClickListener(object :
                DiligenciasTiposAdapter.OnItemClickListener {
                override fun onClick(item: DiligenciaTipo, position: Int, action: String) {
                    dismiss()
                    onItemSelected(item, action)
                }
                override fun onEdit(item: DiligenciaTipo, position: Int) {
                    return
                }
                override fun onDelete(item: DiligenciaTipo, position: Int) {
                    return
                }
            })
        }
    }

    protected abstract fun onItemSelected(item: DiligenciaTipo, action:String)
}