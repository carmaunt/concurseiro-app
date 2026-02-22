package br.com.mauricio.oconcurseiro.data.repository

import br.com.mauricio.oconcurseiro.data.model.Alternativa
import br.com.mauricio.oconcurseiro.data.model.Questao

class QuestaoRepository {

    fun obterQuestao(): Questao {
        return Questao(
            id = "Q3861729",
            disciplina = "Direito Administrativo",
            assunto = "Lei de Acesso à Informação",
            ano = 2025,
            banca = "Instituto Seletiva",
            orgao = "Câmara Municipal de Independência - Ceará",
            enunciado = "Segundo a Lei n° 12.527/2011 (Lei de Acesso à Informação – LAI), a Administração deve assegurar:",
            alternativas = listOf(
                Alternativa("A", "Sigilo como regra geral."),
                Alternativa("B", "Acesso restrito a servidores."),
                Alternativa("C", "Divulgação apenas mediante ordem judicial."),
                Alternativa("D", "Transparência ativa e passiva das informações públicas.")
            )
        )
    }
}