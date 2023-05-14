package com.example.advogo.models.externals

data class CorreioResponse(
    val cep: String,
    val logradouro: String,
    val complemento: String,
    val bairro: String,
    val localidade: String,
    val uf: String,
    val ddd: String

) {
}