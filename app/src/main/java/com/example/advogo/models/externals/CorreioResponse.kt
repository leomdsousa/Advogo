package com.example.advogo.models.externals

data class CorreioResponse(
    private val cep: String,
    private val logradouro: String,
    private val complemento: String,
    private val bairro: String,
    private val localidade: String,
    private val uf: String,
    private val ddd: String

) {
}