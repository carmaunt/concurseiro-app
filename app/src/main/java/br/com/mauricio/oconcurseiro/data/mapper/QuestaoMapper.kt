package br.com.mauricio.oconcurseiro.data.mapper

import br.com.mauricio.oconcurseiro.data.model.Alternativa
import br.com.mauricio.oconcurseiro.data.model.Questao
import br.com.mauricio.oconcurseiro.data.remote.QuestaoDto

object QuestaoMapper {

    fun fromDto(dto: QuestaoDto): Questao {

        val alternativas = parseAlternativas(dto.alternativas).toMutableList()

        if (dto.modalidade.equals("CERTO_ERRADO", ignoreCase = true) && alternativas.isEmpty()) {
            alternativas.add(Alternativa(letra = "C", texto = "Certo"))
            alternativas.add(Alternativa(letra = "E", texto = "Errado"))
        }

        return Questao(
            id = dto.idQuestion,
            disciplina = dto.disciplina,
            assunto = dto.assunto,
            ano = dto.ano,
            banca = dto.banca,
            orgao = dto.instituicao, // backend = instituicao, app = orgao
            enunciado = dto.enunciado,
            questao = dto.questao,
            gabarito = dto.gabarito,
            alternativas = alternativas
        )
    }

    private fun parseAlternativas(raw: String): List<Alternativa> {
        // Espera algo como:
        // "A) texto...\nB) texto...\nC) texto..."
        return raw
            .split("\n")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .mapNotNull { line ->
                // aceita "A)" ou "A." ou "A -"
                val m = Regex("^([A-E])\\s*[\\)\\.-]\\s*(.+)$").find(line)
                    ?: return@mapNotNull null
                val letra = m.groupValues[1]
                val texto = m.groupValues[2].trim()
                Alternativa(letra = letra, texto = texto)
            }
    }
}