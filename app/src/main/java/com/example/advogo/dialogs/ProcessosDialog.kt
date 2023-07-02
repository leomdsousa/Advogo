package com.example.advogo.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.advogo.adapters.ProcessosAdapter
import com.example.advogo.databinding.DialogListBinding
import com.example.advogo.models.Processo

abstract class ProcessosDialog(
    context: Context,
    private var list: ArrayList<Processo>,
    private val titulo: String
): Dialog(context) {
    private lateinit var binding: DialogListBinding
    private var adapter: ProcessosAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState ?: Bundle())

        binding = DialogListBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setupRecyclerView(view)
    }

    private fun setupRecyclerView(view: View) {
        binding.tvTitle.text = titulo

        if (list.size > 0) {
            binding.rvList.layoutManager = LinearLayoutManager(context)
            adapter = ProcessosAdapter(context, list)
            binding.rvList.adapter = adapter

            adapter!!.setOnItemClickListener(object :
                ProcessosAdapter.OnItemClickListener {
                override fun onClick(processo: Processo, position: Int, action: String) {
                    dismiss()
                    onItemSelected(processo, action)
                }
            })
        }
    }

    protected abstract fun onItemSelected(processo: Processo, action:String)
}