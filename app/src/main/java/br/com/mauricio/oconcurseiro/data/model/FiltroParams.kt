package br.com.mauricio.oconcurseiro.data.model

data class FiltroParams(
    val texto: String? = null,
    val disciplina: String? = null,
    val disciplinaId: Long? = null,
    val assunto: String? = null,
    val assuntoId: Long? = null,
    val banca: String? = null,
    val bancaId: Long? = null,
    val instituicao: String? = null,
    val instituicaoId: Long? = null,
    val ano: Int? = null,
    val cargo: String? = null,
    val nivel: String? = null,
    val modalidade: String? = null
)
