package br.com.mauricio.oconcurseiro.data.model

data class Questao(
    val id: String,
    val disciplina: String,
    val disciplinaId: Long?,
    val assunto: String,
    val assuntoId: Long?,
    val ano: Int,
    val banca: String,
    val bancaId: Long?,
    val orgao: String,
    val orgaoId: Long?,
    val cargo: String,
    val nivel: String,
    val modalidade: String,
    val enunciado: String,
    val questao: String,
    val gabarito: String,
    val alternativas: List<Alternativa>
)

data class Alternativa(
    val letra: String,
    val texto: String
)

data class CatalogoItem(
    val id: Long,
    val nome: String
)
