package com.example.advogo.services

import com.example.advogo.models.externals.CorreioResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import javax.inject.Inject

class CorreioApiService @Inject constructor(
    private val apiService: ICorreioApiService
) {
    suspend fun obterEndereco(cep: String): CorreioResponse? {
        val response = apiService.obterEndereco(cep)
        if (response.isSuccessful) {
            return response.body()
        }
        return null
    }
}


interface ICorreioApiService {
    @GET("{cep}/json")
    suspend fun obterEndereco(@Path("cep") cep: String): Response<CorreioResponse>
}

