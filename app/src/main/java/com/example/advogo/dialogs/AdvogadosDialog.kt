package com.example.projmgr.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.advogo.adapters.AdvogadosAdapter
import com.example.advogo.databinding.DialogListBinding
import com.example.advogo.models.Advogado

abstract class AdvogadosDialog(
    context: Context,
    private var list: ArrayList<Advogado>,
    private val titulo: String
): Dialog(context) {
    private lateinit var binding: DialogListBinding
    private var adapter: AdvogadosAdapter? = null

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
            adapter = AdvogadosAdapter(context, list)
            binding.rvList.adapter = adapter

            adapter!!.setOnItemClickListener(object :
                AdvogadosAdapter.OnItemClickListener {
                override fun onClick(position: Int, user: Advogado, action:String) {
                    dismiss()
                    onItemSelected(user, action)
                }
            })
        }
    }

    protected abstract fun onItemSelected(user: Advogado, action:String)
}