package com.example.advogo.utils

object Constants {

    const val ADVOGADOS_TABLE = "advogados"
    const val ADVOGADOS_ID = "_id"
    const val ADVOGADOS_NOME = "nome"
    const val ADVOGADOS_SOBRENOME = "sobrenome"
    const val ADVOGADOS_EMAIL = "email"
    const val ADVOGADOS_OAB = "oab"
    const val ADVOGADOS_ENDERECO = "endereco"
    const val ADVOGADOS_ENDERECO_LAT = "enderecoLat"
    const val ADVOGADOS_ENDEREDO_LONG = "enderecoLong"
    const val ADVOGADOS_TELEFONE = "telefone"

    const val PROCESSOS_TABLE = "processos"
    const val PROCESSOS_ID = "_id"
    const val PROCESSOS_NUMERO = "numero"

    const val PROCESSOS_TIPOS_TABLE = "processosTipos"
    const val PROCESSOS_TIPOS_ID = "_id"
    const val PROCESSOS_TIPOS_TIPO = "tipo"

    const val PROCESSOS_STATUS_TABLE = "processosStatus"
    const val PROCESSOS_STATUS_ID = "_id"
    const val PROCESSOS_STATUS_STATUS = "status"

    const val DILIGENCIAS_TABLE = "diligencias"
    const val DILIGENCIAS_ID = "_id"
    const val DILIGENCIAS_PROCESSO = "processo"
    const val DILIGENCIAS_ADVOGADO = "advogado"

    const val TELEFONES_TABLE = "telefones"
    const val TELEFONES_ID = "_id"
    const val TELEFONES_NUMERO = "numero"
    const val TELEFONES_TIPO = "tipo"

    const val TELEFONES_TIPOS_TABLE = "telefones"
    const val TELEFONES_TIPOS_ID = "_id"
    const val TELEFONES_TIPOS_TIPO = "tipo"

    //Shared
    const val SELECIONAR = "SELECIONAR"
    const val DESELECIONAR = "DESELECIONAR"

    //Informa que a Activity origem está passando um dado
    const val ADV_NOME_PARAM = "advNomeParam"
    const val PROCESSO_ID_PARAM = "processoIdParam"

    //Indica a Activity origem da Intent
    const val FROM_LOGIN_ACTIVITY = "FROM_LOGIN_ACTIVITY"
    const val FROM_PERFIL_ACTIVITY = "FROM_PERFIL_ACTIVITY"
    const val FROM_PROCESSO_CADASTRO_ACTIVITY = "FROM_PROCESSO_CADASTRO_ACTIVITY"

    //Indica para Map Acitivy qual objeto obter
    const val PROCESSO_MAP = "PROCESSO_MAP"
    const val DILIGENCIA_MAP = "DILIGENCIA_MAP"
    const val ADVOGADO_MAP = "ADVOGADO_MAP"

    const val ADVOGO_PREFERENCES: String = "AdvogoPrefs"
}