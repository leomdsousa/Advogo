package com.example.advogo.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.advogo.adapters.DiligenciasStatusAdapter
import com.example.advogo.databinding.DialogListBinding
import com.example.advogo.models.DiligenciaStatus

abstract class DiligenciaStatusDialog(
    context: Context,
    private val list: List<DiligenciaStatus>
): Dialog(context) {
    private lateinit var binding: DialogListBinding
    private var adapter: DiligenciasStatusAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState ?: Bundle())

        binding = DialogListBinding.inflate(layoutInflater)
        binding.tvTitle.text = "Selecione o Status de Diligencia"

        setContentView(binding.root)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setupRecyclerView(binding.root)
    }

    private fun setupRecyclerView(view: View) {
        if (list.isNotEmpty()) {
            binding.rvList.layoutManager = LinearLayoutManager(context)
            adapter = DiligenciasStatusAdapter(context, list)
            binding.rvList.adapter = adapter

            adapter!!.setOnItemClickListener(object :
                DiligenciasStatusAdapter.OnItemClickListener {
                override fun onClick(item: DiligenciaStatus, position: Int, action: String) {
                    dismiss()
                    onItemSelected(item, action)
                }
                override fun onEdit(item: DiligenciaStatus, position: Int) {
                    return
                }
                override fun onDelete(item: DiligenciaStatus, position: Int) {
                    return
                }
            })
        }
    }

    protected abstract fun onItemSelected(item: DiligenciaStatus, action:String)
}