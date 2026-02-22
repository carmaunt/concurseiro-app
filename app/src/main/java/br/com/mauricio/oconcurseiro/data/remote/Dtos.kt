package br.com.mauricio.oconcurseiro.data.remote

data class PageResponse<T>(
    val content: List<T>,
    val number: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val first: Boolean,
    val last: Boolean
)

data class QuestaoDto(
    val idQuestion: String,
    val enunciado: String,
    val questao: String,
    val alternativas: String,
    val disciplina: String,
    val assunto: String,
    val banca: String,
    val instituicao: String,
    val ano: Int,
    val cargo: String,
    val nivel: String,
    val modalidade: String,
    val gabarito: String,
    val criadoEm: String
)