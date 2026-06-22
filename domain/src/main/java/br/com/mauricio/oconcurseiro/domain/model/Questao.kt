package br.com.mauricio.oconcurseiro.domain.model

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
    val alternativas: List<Alternativa>,
    val explicacao: String? = null,
    val questaoImagemConteudo: String? = null,
    val questaoImagemJson: String? = null,
    val textoApoioId: Long? = null,
    val textoApoioTitulo: String? = null,
    val textoApoioConteudo: String? = null,
    val textoApoioTipo: String? = null,
    val textoApoioJson: String? = null
) {
    val isAnulada: Boolean
        get() = gabarito.equals("X", ignoreCase = true)

    val textoAssociado: String
        get() = listOfNotNull(
            textoApoioConteudo?.takeIf { it.isNotBlank() },
            textoApoioJson?.takeIf {
                (
                    textoApoioTipo.equals("TABELA", ignoreCase = true) ||
                        textoApoioTipo.equals("IMAGEM", ignoreCase = true)
                    ) && it.isNotBlank()
            },
            enunciado.takeIf { it.isNotBlank() }
        ).joinToString(separator = "\n\n")
}

data class Alternativa(
    val letra: String,
    val texto: String
)

data class CatalogoItem(
    val id: Long,
    val nome: String
)
