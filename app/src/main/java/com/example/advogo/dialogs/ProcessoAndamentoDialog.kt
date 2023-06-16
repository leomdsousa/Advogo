package com.example.projmgr.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.advogo.adapters.ProcessosAdapter
import com.example.advogo.databinding.DialogListBinding
import com.example.advogo.models.Processo
import com.example.advogo.models.ProcessoAndamento

abstract class ProcessoAndamentoDialog(
    context: Context,
    private var andamento: ProcessoAndamento,
    private val titulo: String
): Dialog(context) {
    private lateinit var binding: DialogListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState ?: Bundle())

        binding = DialogListBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setDados()

        view.setOnClickListener {
//            override fun onSubmit(andamento: ProcessoAndamento) {
//                dismiss()
//                onItemSelected(processo)
//            }
        }

    }

    private fun setDados() {
        binding.tvTitle.text = titulo
    }

    protected abstract fun onSubmit(andamento: ProcessoAndamento)
}