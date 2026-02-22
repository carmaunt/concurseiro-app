package br.com.mauricio.oconcurseiro.data.model

data class Questao(
    val id: String,
    val disciplina: String,
    val assunto: String,
    val ano: Int,
    val banca: String,
    val orgao: String,
    val enunciado: String,
    val questao: String,
    val gabarito: String,
    val alternativas: List<Alternativa>
)

data class Alternativa(
    val letra: String,
    val texto: String
)