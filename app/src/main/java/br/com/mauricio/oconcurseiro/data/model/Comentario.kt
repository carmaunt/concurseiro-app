package br.com.mauricio.oconcurseiro.data.model

data class Comentario(
    val id: Long,
    val questaoId: String,
    val autor: String,
    val texto: String,
    val curtidas: Int,
    val descurtidas: Int,
    val criadoEm: String
)
