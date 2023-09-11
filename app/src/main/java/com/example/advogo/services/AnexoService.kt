package com.example.advogo.services

import android.net.Uri
import android.os.Build
import android.text.TextUtils
import androidx.annotation.RequiresApi
import com.example.advogo.models.Anexo
import com.example.advogo.repositories.IAnexoRepository
import com.example.advogo.utils.UserUtils.getCurrentUserID
import com.example.advogo.utils.extensions.StringExtensions.fromUSADateStringToDate
import com.google.common.io.Files.getFileExtension
import com.google.firebase.Timestamp
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class AnexoService {
    @Inject lateinit var repository: IAnexoRepository

    @RequiresApi(Build.VERSION_CODES.O)
    fun cadastrar(
        anexo: Anexo,
        onSuccess: () -> Unit,
        onFailure: (List<String>) -> Unit
    ) {
        val erros = validar(anexo)

        if (erros.isNotEmpty()) {
            onFailure(erros)
        }

        CoroutineScope(Dispatchers.Main).launch {
            val uri = if (anexo.imagemSelecionadaURI != null) {
                salvarAnexo("", anexo.imagemSelecionadaURI!!)
            } else {
                null
            }

            val input = Anexo(
                id = "",
                descricao = anexo.descricao,
                nome = anexo.imagemSelecionadaURI!!.lastPathSegment,
                uri = uri,
                advogado = getCurrentUserID(),
                data = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                dataTimestamp = Timestamp.now(),
                processo = anexo.processo
            )

            repository.adicionarAnexo(
                input,
                { onSuccess() },
                { onFailure(listOf("Falha ao salvar anexo do processo.")) }
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun atualizar(
        anexo: Anexo,
        onSuccess: () -> Unit,
        onFailure: (List<String>) -> Unit
    ) {
        val erros = validar(anexo)

        if (erros.isNotEmpty()) {
            return onFailure(erros)
        }

        CoroutineScope(Dispatchers.Main).launch {
            val uri = if (anexo.imagemSelecionadaURI != null) {
                salvarAnexo(anexo.id, anexo.imagemSelecionadaURI!!)
            } else {
                null
            }

            val input = Anexo(
                id = anexo.id,
                descricao = anexo.descricao,
                nome = anexo.imagemSelecionadaURI!!.lastPathSegment,
                uri = uri,
                advogado = getCurrentUserID(),
                data = anexo.data,
                processo = anexo.processo
            )

            anexo.dataTimestamp = Timestamp(anexo.data!!.fromUSADateStringToDate())

            repository.atualizarAnexo(
                input,
                { onSuccess() },
                { onFailure(listOf("Falha ao salvar anexo do processo.")) }
            )
        }
    }

    private suspend fun salvarAnexo(id: String, imagemSelecionadaURI: Uri): String {
        return suspendCancellableCoroutine { continuation ->
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "PROCESSO_${id}_ANEXO" + System.currentTimeMillis() + "."
                        + getFileExtension(imagemSelecionadaURI.lastPathSegment!!)
            )

            sRef.putFile(imagemSelecionadaURI)
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

    private fun validar(anexo: Anexo): List<String> {
        val errors = mutableListOf<String>()

        if (TextUtils.isEmpty(anexo.descricao)) {
            errors.add("A descrição do anexo é obrigatória.")
        }

        if (anexo.imagemSelecionadaURI == null) {
            errors.add("O anexo é obrigatória.")
        }

        return errors
    }

    fun obterAnexos(
        onSuccessListener: (List<Anexo>) -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.obterAnexos(onSuccessListener, onFailureListener)
    }

    fun obterAnexosPorProcesso(
        numeroProcesso: String,
        onSuccessListener: (List<Anexo>) -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.obterAnexosPorProcesso(numeroProcesso, onSuccessListener, onFailureListener)
    }

    fun obterAnexo(
        id: String,
        onSuccessListener: (Anexo: Anexo) -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.obterAnexo(id, onSuccessListener, onFailureListener)
    }

    suspend fun obterAnexosPorLista(ids: List<String>): List<Anexo>? {
        return repository.obterAnexosPorLista(ids)
    }

    suspend fun obterAnexosPorProcesso(numeroProcesso: String): List<Anexo>? {
        return repository.obterAnexosPorProcesso(numeroProcesso)
    }
}