package com.example.advogo.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.advogo.R
import com.example.advogo.adapters.AnexosAdapter
import com.example.advogo.databinding.DialogProcessoAnexoBinding
import com.example.advogo.databinding.FragmentProcessoAnexoBinding
import com.example.advogo.models.Anexo
import com.example.advogo.models.Processo
import com.example.advogo.repositories.IAnexoRepository
import com.example.advogo.utils.Constants
import com.example.advogo.dialogs.ProcessoAnexoDialog
import com.example.advogo.utils.extensions.ConverterUtils.fromUSADateStringToDate
import com.google.firebase.Timestamp
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ProcessoAnexoFragment : BaseFragment() {
    private lateinit var binding: FragmentProcessoAnexoBinding
    private lateinit var bindingDialog: DialogProcessoAnexoBinding
    @Inject lateinit var anexoRepository: IAnexoRepository
    private lateinit var processoDetalhes: Processo

    private var selectedFile: Uri? = null
    private var selectedFileName: String? = null

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProcessoAnexoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        obterIntentDados()

        setAnexosToUI(processoDetalhes.anexosLista)

        binding.fabAddAnexo.setOnClickListener {
            anexoProcessoDialog(null)
        }

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data!!.hasExtra(Constants.FROM_ANEXOS_ACTIVITY)) {
                    anexoRepository.obterAnexos(
                        { lista -> setAnexosToUI(lista as ArrayList<Anexo>) },
                        { ex -> null } //TODO("Imlementar OnFailure")
                    )
                }
                else if(result.data != null && result.data!!.data != null) {
                    result.data?.data?.let { uri ->
                        selectedFile = uri

                        selectedFileName = getFileNameFromUri(uri)
                        bindingDialog.tvNomeArquivoSelecionado.text = selectedFileName
                    }
                }
            } else {
                Log.e("Cancelado", "Cancelado")
            }
        }
    }

    private fun obterIntentDados() {
        if (requireActivity().intent.hasExtra(Constants.PROCESSO_PARAM)) {
            processoDetalhes = requireActivity().intent.getParcelableExtra(Constants.PROCESSO_PARAM)!!
        }
    }

    private fun setAnexosToUI(lista: List<Anexo>?) {
        if(lista != null && lista.isNotEmpty()) {
            binding.rvAnexosLista.visibility = View.VISIBLE
            binding.tvNenhumAnexoDisponivel.visibility = View.GONE

            binding.rvAnexosLista.layoutManager = LinearLayoutManager(binding.root.context)
            binding.rvAnexosLista.setHasFixedSize(true)

            val adapter = AnexosAdapter(binding.root.context, lista)
            binding.rvAnexosLista.adapter = adapter

            adapter.setOnItemClickListener(object :
                AnexosAdapter.OnItemClickListener {
                override fun onClick(anexo: Anexo) {
                    anexoProcessoDialog(anexo)
                }
                override fun onView(anexo: Anexo, position: Int) {
                    if(!TextUtils.isEmpty(anexo.uri)) {
                        abrirArquivo(anexo.uri!!)
                    }
                }
                override fun onDelete(anexo: Anexo, position: Int) {
                    if(!TextUtils.isEmpty(anexo.uri)) {
                        showProgressDialog(getString(R.string.aguardePorfavor))

                        deletarArquivo(
                            anexo.uri!!
                        ) {
                            deleteAnexoSuccess()
                            hideProgressDialog()
                        }
                    }
                }
            })

        } else {
            binding.rvAnexosLista.visibility = View.GONE
            binding.tvNenhumAnexoDisponivel.visibility = View.VISIBLE
        }
    }

    private fun anexoProcessoDialog(anexo: Anexo? = null) {
        bindingDialog = DialogProcessoAnexoBinding.inflate(layoutInflater)

        val dialog = object : ProcessoAnexoDialog(
            requireContext(),
            anexo ?: Anexo(),
            bindingDialog
        ) {
            override fun onChooseFile() {
                showFileChooser(resultLauncher)
            }
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onSubmit(anexo: Anexo) {
                if(anexo.id.isBlank()) {
                    adicionarAnexo(anexo)
                } else {
                    atualizarAnexo(anexo)
                }
            }
        }

        dialog.show()

        //bindingDialog = DialogProcessoAnexoBinding.inflate(dialog.layoutInflater)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun atualizarAnexo(anexo: Anexo) {
        if(!validarFormulario()) {
            return
        }

        showProgressDialog(getString(R.string.aguardePorfavor))

        CoroutineScope(Dispatchers.Main).launch {
            val uri = if (selectedFile != null) {
                salvarAnexoProcesso()
            } else {
                null
            }

            val anexo = Anexo(
                id = processoDetalhes.id,
                descricao = bindingDialog.etDescricaoAnexo.text.toString(),
                nome = selectedFile?.let { getFileNameFromUri(it) },
                uri = uri,
                advogado = getCurrentUserID(),
                data = anexo.data,
                processo = processoDetalhes.numero!!
            )

            anexo.dataTimestamp = Timestamp(anexo.data!!.fromUSADateStringToDate())

            anexoRepository.atualizarAnexo(
                anexo,
                { saveAnexoSuccess() },
                { saveAnexoFailure() }
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun adicionarAnexo(anexo: Anexo) {
        if(!validarFormulario()) {
            return
        }

        showProgressDialog(getString(R.string.aguardePorfavor))

        CoroutineScope(Dispatchers.Main).launch {
            val uri = if (selectedFile != null) {
                salvarAnexoProcesso()
            } else {
                null
            }

            val anexo = Anexo(
                id = processoDetalhes.id,
                descricao = bindingDialog.etDescricaoAnexo.text.toString(),
                nome = selectedFile?.let { getFileNameFromUri(it) },
                uri = uri,
                advogado = getCurrentUserID(),
                data = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                dataTimestamp = Timestamp.now(),
                processo = processoDetalhes.numero!!
            )

            anexoRepository.adicionarAnexo(
                anexo,
                { saveAnexoSuccess() },
                { saveAnexoFailure() }
            )
        }
    }

    private fun saveAnexoSuccess() {
        anexoRepository.obterAnexosPorProcesso(
            processoDetalhes.numero!!,
            {
                setAnexosToUI(it as ArrayList<Anexo>)
                hideProgressDialog()
            },
            { hideProgressDialog() }
        )
    }

    private fun saveAnexoFailure() {
        hideProgressDialog()

        Toast.makeText(
            requireContext(),
            "Erro para salvar o anexo!",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun deleteAnexoSuccess() {
        anexoRepository.obterAnexos(
            {
                setAnexosToUI(it as ArrayList<Anexo>)
                hideProgressDialog()
            },
            { hideProgressDialog() }
        )
    }

    private fun validarFormulario(): Boolean {
        var validado = true

        if (TextUtils.isEmpty(bindingDialog.etDescricaoAnexo.text.toString())) {
            bindingDialog.etDescricaoAnexo.error = "Obrigatório"
            bindingDialog.etDescricaoAnexo.requestFocus()
            validado = false
        }

//        if (TextUtils.isEmpty(bindingDialog.etDataAndamento.text.toString())) {
//            bindingDialog.etDataAndamento.error = "Obrigatório"
//            bindingDialog.etDataAndamento.requestFocus()
//            validado = false
//        }

         return validado
    }

    private suspend fun salvarAnexoProcesso(): String {
        return suspendCancellableCoroutine { continuation ->
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "PROCESSO_${id}_ANEXO" + System.currentTimeMillis() + "."
                        + getFileExtension(selectedFile!!)
            )

            sRef.putFile(selectedFile!!)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { uri ->
                            val url = uri.toString()
                            continuation.resume(url, null)
                        }
                        .addOnFailureListener { exception ->
                            continuation.cancel(exception)
                        }
                }
                .addOnFailureListener { exception ->
                    continuation.cancel(exception)
                }
        }
    }
}