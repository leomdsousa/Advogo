package com.example.advogo.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.advogo.adapters.ClientesAdapter
import com.example.advogo.databinding.DialogListBinding
import com.example.advogo.models.Cliente

abstract class ClientesDialog(
    context: Context,
    private var list: ArrayList<Cliente>,
    private val titulo: String
): Dialog(context) {
    private lateinit var binding: DialogListBinding
    private var adapter: ClientesAdapter? = null

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
            adapter = ClientesAdapter(context, list)
            binding.rvList.adapter = adapter

            adapter!!.setOnItemClickListener(object :
                ClientesAdapter.OnItemClickListener {
                override fun onClick(cliente: Cliente, position: Int, acao: String?) {
                    dismiss()
                    onItemSelected(cliente, acao!!)
                }
            })
        }
    }

    protected abstract fun onItemSelected(cliente: Cliente, action:String)
}