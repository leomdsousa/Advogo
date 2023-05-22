package com.example.projmgr.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.advogo.adapters.DiligenciasAdapter
import com.example.advogo.databinding.DialogListBinding
import com.example.advogo.models.Diligencia

abstract class DiligenciasDialog(
    context: Context,
    private var list: ArrayList<Diligencia>,
    private val titulo: String
): Dialog(context) {
    private lateinit var binding: DialogListBinding
    private var adapter: DiligenciasAdapter? = null

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
            adapter = DiligenciasAdapter(context, list)
            binding.rvList.adapter = adapter

            adapter!!.setOnItemClickListener(object :
                DiligenciasAdapter.OnItemClickListener {
                override fun onClick(diligencia: Diligencia, position: Int) {
                    dismiss()
                    onItemSelected(diligencia)
                }
            })
        }
    }

    protected abstract fun onItemSelected(Diligencia: Diligencia)
}