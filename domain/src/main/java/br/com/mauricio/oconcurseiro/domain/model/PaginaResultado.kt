package br.com.mauricio.oconcurseiro.domain.model

data class PaginaResultado<T>(
    val content: List<T>,
    val number: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val first: Boolean,
    val last: Boolean
)