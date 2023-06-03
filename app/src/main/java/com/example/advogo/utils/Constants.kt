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

    const val CLIENTES_TABLE = "clientes"
    const val CLIENTES_ID = "_id"
    const val CLIENTES_TELEFONE = "telefone"
    const val CLIENTES_EMAIL = "email"

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
    const val DILIGENCIAS_DATA = "data"

    const val DILIGENCIAS_STATUS_TABLE = "diligenciasStatus"
    const val DILIGENCIAS_STATUS_ID = "_id"
    const val DILIGENCIAS_STATUS_STATUS = "status"

    const val DILIGENCIAS_TIPOS_TABLE = "diligenciasTipos"
    const val DILIGENCIAS_TIPOS_ID = "_id"
    const val DILIGENCIAS_TIPOS_TIPO = "tipo"

    const val TELEFONES_TABLE = "telefones"
    const val TELEFONES_ID = "_id"
    const val TELEFONES_NUMERO = "numero"
    const val TELEFONES_TIPO = "tipo"

    const val TELEFONES_TIPOS_TABLE = "telefones"
    const val TELEFONES_TIPOS_ID = "_id"
    const val TELEFONES_TIPOS_TIPO = "tipo"

    const val ENDERECOS_TABLE = "enderecos"

    const val ANEXOS_TABLE = "anexos"
    const val ANEXOS_ID = "_id"
    const val ANEXOS_NOME = "nome"
    const val ANEXOS_URI = "uri"

    //Shared
    const val SELECIONAR = "SELECIONAR"
    const val DESELECIONAR = "DESELECIONAR"

    //Informa que a Activity origem está passando um dado
    const val ADV_NOME_PARAM = "advNomeParam"
    const val ADV_ID_PARAM = "advIdParam"
    const val PROCESSO_ID_PARAM = "processoIdParam"
    const val CLIENTE_ID_PARAM = "clienteIdParam"
    const val DILIGENCIA_ID_PARAM = "diligenciaIdParam"

    const val ADV_PARAM = "advParam"
    const val PROCESSO_PARAM = "processoParam"
    const val CLIENTE_PARAM = "clienteParam"
    const val DILIGENCIA_PARAM = "diligenciaParam"

    //Indica a Activity origem da Intent
    const val FROM_LOGIN_ACTIVITY = "FROM_LOGIN_ACTIVITY"
    const val FROM_PERFIL_ACTIVITY = "FROM_PERFIL_ACTIVITY"
    const val FROM_PROCESSO_ACTIVITY = "FROM_PROCESSO_ACTIVITY"
    const val FROM_CLIENTE_ACTIVITY = "FROM_CLIENTE_ACTIVITY"
    const val FROM_DILIGENCIA_ACTIVITY = "FROM_DILIGENCIA_ACTIVITY"
    const val FROM_GOOGLE_PLACES = "FROM_GOOGLE_PLACES"
    const val FROM_DEVICE_GALLERY = "FROM_DEVICE_GALLERY"
    const val FROM_DEVICE_CAMERA = "FROM_DEVICE_CAMERA"
    const val FROM_REGISTRAR_ACTIVITY = "FROM_REGISTRAR_ACTIVITY"

    //Indica para Map Activity qual objeto obter
    const val PROCESSO_MAP = "PROCESSO_MAP"
    const val DILIGENCIA_MAP = "DILIGENCIA_MAP"
    const val ADVOGADO_MAP = "ADVOGADO_MAP"

    const val ADVOGO_PREFERENCES: String = "AdvogoPrefs"
}