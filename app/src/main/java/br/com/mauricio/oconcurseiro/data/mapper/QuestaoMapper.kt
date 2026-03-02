package br.com.mauricio.oconcurseiro.data.mapper

import br.com.mauricio.oconcurseiro.data.model.Alternativa
import br.com.mauricio.oconcurseiro.data.model.CatalogoItem
import br.com.mauricio.oconcurseiro.data.model.Questao
import br.com.mauricio.oconcurseiro.data.remote.CatalogoItemDto
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
            disciplinaId = dto.disciplinaId,
            assunto = dto.assunto,
            assuntoId = dto.assuntoId,
            ano = dto.ano,
            banca = dto.banca,
            bancaId = dto.bancaId,
            orgao = dto.instituicao,
            orgaoId = dto.instituicaoId,
            cargo = dto.cargo,
            nivel = dto.nivel,
            modalidade = dto.modalidade,
            enunciado = dto.enunciado,
            questao = dto.questao,
            gabarito = dto.gabarito,
            alternativas = alternativas
        )
    }

    fun catalogoFromDto(dto: CatalogoItemDto): CatalogoItem {
        return CatalogoItem(id = dto.id, nome = dto.nome)
    }

    private fun parseAlternativas(raw: String): List<Alternativa> {
        return raw
            .split("\n")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .mapNotNull { line ->
                val m = Regex("^([A-E])\\s*[\\)\\.-]\\s*(.+)$").find(line)
                    ?: return@mapNotNull null
                val letra = m.groupValues[1]
                val texto = m.groupValues[2].trim()
                Alternativa(letra = letra, texto = texto)
            }
    }
}
