package com.example.advogo.services

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.advogo.models.Advogado
import com.example.advogo.repositories.IAdvogadoRepository
import com.example.advogo.utils.extensions.StringExtensions.fromUSADateStringToDate
import com.google.common.base.Strings
import com.google.common.io.Files.getFileExtension
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class AdvogadosService {
    @Inject lateinit var repository: IAdvogadoRepository

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    fun cadastrar(
        advogado: Advogado,
        onSuccess: () -> Unit,
        onFailure: (List<String>) -> Unit
    ) {
        val erros = validar(advogado)

        if (erros.isNotEmpty()) {
            onFailure(erros)
            return
        }

        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(advogado.email!!, advogado.password!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = task.result!!.user
                    advogado.id = firebaseUser!!.uid
                    advogado.dataCriacao = SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(LocalDateTime.now())
                    advogado.dataCriacaoTimestamp = Timestamp.now()
                    advogado.dataAlteracao = null
                    advogado.dataAlteracaoTimestamp = null

                    repository.adicionarAdvogado(
                        advogado,
                        { onSuccess() },
                        { onFailure(listOf("Erro ao cadastrar Advogado")) }
                    )
                } else {
                    onFailure(listOf("Erro ao cadastrar Advogado"))
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun atualizar(
        advogado: Advogado,
        onSuccess: () -> Unit,
        onFailure: (List<String>) -> Unit
    ) {
        val erros = validar(advogado)

        if (erros.isNotEmpty()) {
            onFailure(erros)
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            val imageUrl = if (advogado.imagemSelecionadaURI != null) {
                salvarImagem(advogado.oab!!.toString(), advogado.imagemSelecionadaURI!!)
            } else {
                advogado.imagem
            }

            advogado.dataAlteracao = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            advogado.dataAlteracaoTimestamp = Timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).fromUSADateStringToDate())
            advogado.imagem = imageUrl

            try {
                repository.atualizarAdvogado(
                    advogado,
                    { onSuccess() },
                    { onFailure(listOf("Erro ao atualizar Advogado")) }
                )
            } catch (e: Exception) {
                onFailure(listOf("Erro ao atualizar Advogado"))
            }
        }
    }

    private suspend fun salvarImagem(oab: String, imagemSelecionadaUri: Uri): String {
        return suspendCancellableCoroutine { continuation ->
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "ADVOGADO_${oab}_IMAGEM" + System.currentTimeMillis() + "." + getFileExtension(
                    imagemSelecionadaUri.lastPathSegment!!
                )
            )

            sRef.putFile(imagemSelecionadaUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { uri ->
                            val imageUrl = uri.toString()
                            continuation.resume(imageUrl, null)
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

    fun deletar(advogadoId: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        repository.deletarAdvogado(
            advogadoId,
            { onSuccess() },
            { onFailure() }
        )
    }

    private fun validar(advogado: Advogado): List<String> {
        val erros = mutableListOf<String>()

        if (Strings.isNullOrEmpty(advogado.nome)) {
            erros.add("O nome é obrigatório")
        }

        if (Strings.isNullOrEmpty(advogado.sobrenome)) {
            erros.add("O sobrenome é obrigatório")
        }

        if (Strings.isNullOrEmpty(advogado.email)) {
            erros.add("O email é obrigatório")
        }

        if (Strings.isNullOrEmpty(advogado.telefone)) {
            erros.add("O telefone é obrigatório")
        }

        if (advogado.oab == null) {
            erros.add("O número da OAB é obrigatório")
        }

        if (Strings.isNullOrEmpty(advogado.endereco)) {
            erros.add("O endereço é obrigatório")
        }

        return erros
    }

    fun obterAdvogados(
        onSuccessListener: (lista: List<Advogado>) -> Unit,
        onFailureListener: (exception: Exception?) -> Unit
    ) {
        repository.obterAdvogados(onSuccessListener, onFailureListener)
    }

    fun obterAdvogado(
        id: String,
        onSuccessListener: (advogado: Advogado) -> Unit,
        onFailureListener: (exception: Exception?) -> Unit
    ) {
        repository.obterAdvogado(id, onSuccessListener, onFailureListener)
    }

    fun obterAdvogadoPorEmail(
        email: String,
        onSuccessListener: (advogado: Advogado) -> Unit,
        onFailureListener: (exception: Exception?) -> Unit
    ) {
        repository.obterAdvogadoPorEmail(email, onSuccessListener, onFailureListener)
    }

    suspend fun obterAdvogados(): List<Advogado>? {
        return repository.obterAdvogados()
    }

    suspend fun obterAdvogado(id: String): Advogado? {
        return repository.obterAdvogado(id)
    }
}