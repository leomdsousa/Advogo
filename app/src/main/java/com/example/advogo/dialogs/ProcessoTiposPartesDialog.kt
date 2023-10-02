package com.example.advogo.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.advogo.adapters.TiposPartesAdapter
import com.example.advogo.databinding.DialogListBinding
import com.example.advogo.models.TiposPartes

abstract class ProcessoTiposPartesDialog(
    context: Context,
    private val list: List<TiposPartes>
): Dialog(context) {
    private lateinit var binding: DialogListBinding
    private var adapter: TiposPartesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState ?: Bundle())

        binding = DialogListBinding.inflate(layoutInflater)
        binding.tvTitle.text = "Selecione o tipo de Parte"

        setContentView(binding.root)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setupRecyclerView(binding.root)
    }

    private fun setupRecyclerView(view: View) {
        if (list.isNotEmpty()) {
            binding.rvList.layoutManager = LinearLayoutManager(context)
            adapter = TiposPartesAdapter(context, list)
            binding.rvList.adapter = adapter

            adapter!!.setOnItemClickListener(object :
                TiposPartesAdapter.OnItemClickListener {
                override fun onClick(tipoParte: TiposPartes, position: Int, action: String) {
                    dismiss()
                    onItemSelected(tipoParte, action)
                }
            })
        }
    }

    protected abstract fun onItemSelected(tipoParte: TiposPartes, action:String)
}