package com.example.advogo.dialogs.form

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import com.example.advogo.R
import com.example.advogo.databinding.DialogProcessoAnexoBinding
import com.example.advogo.models.*
import com.example.advogo.utils.DialogUtils
import com.google.firebase.Timestamp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

abstract class ProcessoAnexoDialog(
    context: Context,
    private var anexo: Anexo,
    private val binding: DialogProcessoAnexoBinding,
    private val readOnly: Boolean = false
): Dialog(context) {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState ?: Bundle())

        setContentView(binding.root)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setDados(anexo)

        if(readOnly) {
            binding.btnSubmitProcessoAnexo.visibility = View.GONE
            DialogUtils.makeEditTextsReadOnly(this, R.layout.dialog_processo_anexo)
        }

        binding.btnSelecionarArquivo.setOnClickListener {
            onChooseFile()
        }

        binding.btnSubmitProcessoAnexo.setOnClickListener {
            dismiss()

            var anexo = Anexo(
                id = anexo.id,
                nome = anexo.nome,
                uri = anexo.uri,
                descricao = binding.etDescricaoAnexo.text.toString(),
                data =
                       if (anexo.id.isNullOrEmpty())
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                       else anexo.data!!,
                dataTimestamp =
                       if (anexo.id.isNullOrEmpty()) Timestamp.now()
                       else anexo.dataTimestamp!!,
                advogado = anexo.advogado,
                processo = anexo.processo
            )

            onSubmit(anexo)
        }
    }

    private fun setDados(anexo: Anexo) {
        if(anexo.id.isBlank()) {
            binding.tvTitle.text = "Cadastro Anexo"
            binding.btnSubmitProcessoAnexo.text = "Cadastrar"
        } else {
            binding.tvTitle.text = "Detalhes Anexo"
            binding.btnSubmitProcessoAnexo.text = "Atualizar"

            binding.etDescricaoAnexo.setText(anexo.descricao)
        }
    }

    protected abstract fun onChooseFile()
    protected abstract fun onSubmit(anexo: Anexo)
}