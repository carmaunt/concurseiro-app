package br.com.mauricio.oconcurseiro.data.remote

data class ApiResponse<T>(
    val success: Boolean,
    val data: T,
    val timestamp: String? = null,
    val path: String? = null
)

data class PageResponse<T>(
    val content: List<T>,
    val number: Int = 0,
    val size: Int = 0,
    val totalElements: Long = 0,
    val totalPages: Int = 0,
    val first: Boolean = true,
    val last: Boolean = true,
    val page: PageInfo? = null
) {
    val resolvedTotalElements: Long
        get() = page?.totalElements ?: totalElements

    val resolvedTotalPages: Int
        get() = page?.totalPages ?: totalPages

    val resolvedLast: Boolean
        get() = if (page != null) {
            (page.number + 1) >= page.totalPages
        } else {
            last
        }
}

data class PageInfo(
    val size: Int = 0,
    val number: Int = 0,
    val totalElements: Long = 0,
    val totalPages: Int = 0
)

data class QuestaoDto(
    val idQuestion: String,
    val enunciado: String,
    val questao: String,
    val alternativas: String,
    val disciplina: String,
    val disciplinaId: Long?,
    val assunto: String,
    val assuntoId: Long?,
    val banca: String,
    val bancaId: Long?,
    val instituicao: String,
    val instituicaoId: Long?,
    val ano: Int,
    val cargo: String,
    val nivel: String,
    val modalidade: String,
    val gabarito: String,
    val criadoEm: String
)

data class CatalogoItemDto(
    val id: Long,
    val nome: String
)

data class ComentarioResponseDto(
    val id: Long,
    val questaoId: String,
    val autor: String,
    val texto: String,
    val curtidas: Int,
    val descurtidas: Int,
    val criadoEm: String
)

data class ComentarioRequestDto(
    val autor: String,
    val texto: String
)
