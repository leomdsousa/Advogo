package com.example.advogo.utils.constants

object Constants {
    const val ADVOGADOS_TABLE = "advogados"
    const val ADVOGADOS_ID = "id"
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
    const val CLIENTES_NOME = "nome"
    const val CLIENTES_TELEFONE = "telefone"
    const val CLIENTES_EMAIL = "email"

    const val PROCESSOS_TABLE = "processos"
    const val PROCESSOS_ID = "_id"
    const val PROCESSOS_TITULO = "titulo"
    const val PROCESSOS_NUMERO = "numero"
    const val PROCESSOS_DATA_TIMESTAMP = "dataTimestamp"

    const val PROCESSOS_TIPOS_TABLE = "processosTipos"
    const val PROCESSOS_TIPOS_ID = "_id"
    const val PROCESSOS_TIPOS_TIPO = "tipo"

    const val PROCESSOS_STATUS_TABLE = "processosStatus"
    const val PROCESSOS_STATUS_ID = "_id"
    const val PROCESSOS_STATUS_STATUS = "status"

    const val PROCESSOS_ANDAMENTOS_TABLE = "processosAndamentos"
    const val PROCESSOS_ANDAMENTOS_PROCESSO = "processo"
    const val PROCESSOS_ANDAMENTOS_DATA = "data"
    const val PROCESSOS_ANDAMENTOS_DATA_TIMESTAMP = "dataTimestamp"

    const val PROCESSOS_STATUS_ANDAMENTOS_TABLE = "processosStatusAndamentos"

    const val PROCESSOS_TIPOS_ANDAMENTOS_TABLE = "processosTiposAndamentos"

    const val PROCESSOS_HISTORICOS_TABLE = "processosHistoricos"
    const val PROCESSOS_HISTORICOS_PROCESSO = "processo"
    const val PROCESSOS_HISTORICOS_DATA = "data"
    const val PROCESSOS_HISTORICOS_DATA_TIMESTAMP = "dataTimestamp"

    const val DILIGENCIAS_TABLE = "diligencias"
    const val DILIGENCIAS_ID = "_id"
    const val DILIGENCIAS_DESCRICAO = "descricao"
    const val DILIGENCIAS_PROCESSO = "processo"
    const val DILIGENCIAS_ADVOGADO = "advogado"
    const val DILIGENCIAS_DATA = "data"
    const val DILIGENCIAS_DATA_TIMESTAMP = "dataTimestamp"

    const val DILIGENCIAS_STATUS_TABLE = "diligenciasStatus"
    const val DILIGENCIAS_STATUS_ID = "_id"
    const val DILIGENCIAS_STATUS_STATUS = "status"

    const val DILIGENCIAS_TIPOS_TABLE = "diligenciasTipos"
    const val DILIGENCIAS_TIPOS_ID = "_id"
    const val DILIGENCIAS_TIPOS_TIPO = "tipo"

    const val DILIGENCIAS_HISTORICOS_TABLE = "diligenciasHistoricos"
    const val DILIGENCIAS_HISTORICOS_DILIGENCIA = "diligencia"
    const val DILIGENCIAS_HISTORICOS_DATA = "data"
    const val DILIGENCIAS_HISTORICOS_DATA_TIMESTAMP = "dataTimestamp"

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
    const val ANEXOS_PROCESSO = "processo"
    const val ANEXOS_DATA = "data"
    const val ANEXOS_DATA_TIMESTAMP = "dataTimestamp"

    const val TIPOS_PARTES_TABLE = "tiposPartes"

    const val TIPOS_HONORARIOS_TABLE = "tiposHonorarios"

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
    const val FROM_ADVOGADO_ACTIVITY = "FROM_ADVOGADO_ACTIVITY"
    const val FROM_PROCESSO_ACTIVITY = "FROM_PROCESSO_ACTIVITY"
    const val FROM_CLIENTE_ACTIVITY = "FROM_CLIENTE_ACTIVITY"
    const val FROM_DILIGENCIA_ACTIVITY = "FROM_DILIGENCIA_ACTIVITY"
    const val FROM_GOOGLE_PLACES = "FROM_GOOGLE_PLACES"
    const val FROM_DEVICE_GALLERY = "FROM_DEVICE_GALLERY"
    const val FROM_DEVICE_CAMERA = "FROM_DEVICE_CAMERA"
    const val FROM_REGISTRAR_ACTIVITY = "FROM_REGISTRAR_ACTIVITY"
    const val FROM_ANEXOS_ACTIVITY = "FROM_ANEXOS_ACTIVITY"
    const val FROM_ANDAMENTOS_ACTIVITY = "FROM_ANDAMENTOS_ACTIVITY"
    const val FROM_FILE_CHOOSE = "FROM_FILE_CHOOSE"

    //Indica para Map Activity qual objeto obter
    const val PROCESSO_MAP = "PROCESSO_MAP"
    const val DILIGENCIA_MAP = "DILIGENCIA_MAP"
    const val ADVOGADO_MAP = "ADVOGADO_MAP"

    const val ADVOGO_PREFERENCES: String = "AdvogoPrefs"

    const val FCM_TOKEN:String = "fcmToken"
    const val FCM_TOKEN_UPDATED:String = "fcmTokenUpdated"
    const val FCM_BASE_URL:String = "https://fcm.googleapis.com/fcm/send"
    const val FCM_AUTHORIZATION:String = "authorization"
    const val FCM_KEY:String = "key"
    //const val FCM_SERVER_KEY:String = "BJk1N2tXH6jEUZh-o4hzqjnODU2BR3IEvU1Z15bR96QVsBv3jJkU5u5Tk2XRfnLYa1EKjzPeRQG3YUfXt-KeqVo"
    const val FCM_SERVER_KEY:String = "AAAAXZJFr08:APA91bGRCl933I5Eh_Djv_Dl__PBkWB85z4Ww1nIjDUHomcqt61PbXvLvPwvMyHDGcgNFBjhOegsbp7RKEeuCZxagTMe8-k-N7foEutSLRD_YBTapRd771BSh1KyIGW8DjQ5oHISQwOJ"
    const val FCM_KEY_TITLE:String = "title"
    const val FCM_KEY_MESSAGE:String = "message"
    const val FCM_KEY_DATA:String = "data"
    const val FCM_KEY_TO:String = "to"
}