package br.com.mauricio.oconcurseiro.data.model

data class FiltroParams(
    val texto: String? = null,
    val disciplina: String? = null,
    val banca: String? = null,
    val ano: Int? = null
)