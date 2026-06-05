package br.com.mauricio.oconcurseiro.data.mapper

import br.com.mauricio.oconcurseiro.data.remote.QuestaoDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuestaoMapperTest {

    private fun dtoBase(
        modalidade: String = "MULTIPLA_ESCOLHA",
        alternativas: String = "",
        gabarito: String = "B"
    ) = QuestaoDto(
        idQuestion = "q-001",
        enunciado = "Texto do enunciado",
        questao = "Qual a capital do Brasil?",
        alternativas = alternativas,
        disciplina = "Geografia",
        disciplinaId = 1L,
        assunto = "Capitais",
        assuntoId = 2L,
        banca = "CESPE",
        bancaId = 3L,
        instituicao = "IBGE",
        instituicaoId = 4L,
        ano = 2023,
        cargo = "Analista",
        nivel = "SUPERIOR",
        modalidade = modalidade,
        gabarito = gabarito,
        criadoEm = "2023-01-01T00:00:00"
    )

    @Test
    fun `fromDto mapeia campos basicos corretamente`() {
        val dto = dtoBase()
        val questao = QuestaoMapper.fromDto(dto)

        assertEquals("q-001", questao.id)
        assertEquals("Geografia", questao.disciplina)
        assertEquals("Capitais", questao.assunto)
        assertEquals("CESPE", questao.banca)
        assertEquals("IBGE", questao.orgao)
        assertEquals(2023, questao.ano)
        assertEquals("Analista", questao.cargo)
        assertEquals("SUPERIOR", questao.nivel)
        assertEquals("Qual a capital do Brasil?", questao.questao)
        assertEquals("B", questao.gabarito)
    }

    @Test
    fun `fromDto parse alternativas no formato A) texto`() {
        val raw = """
            A) Brasília
            B) São Paulo
            C) Rio de Janeiro
            D) Salvador
            E) Manaus
        """.trimIndent()
        val questao = QuestaoMapper.fromDto(dtoBase(alternativas = raw))

        assertEquals(5, questao.alternativas.size)
        assertEquals("A", questao.alternativas[0].letra)
        assertEquals("Brasília", questao.alternativas[0].texto)
        assertEquals("B", questao.alternativas[1].letra)
        assertEquals("C", questao.alternativas[2].letra)
        assertEquals("D", questao.alternativas[3].letra)
        assertEquals("E", questao.alternativas[4].letra)
    }

    @Test
    fun `fromDto parse alternativas no formato A- texto`() {
        val raw = "A- Opção um\nB- Opção dois\nC- Opção três"
        val questao = QuestaoMapper.fromDto(dtoBase(alternativas = raw))

        assertEquals(3, questao.alternativas.size)
        assertEquals("Opção um", questao.alternativas[0].texto)
    }

    @Test
    fun `fromDto ignora linhas em branco e mal formatadas`() {
        val raw = """
            A) Primeira
            
            linha solta sem letra
            B) Segunda
        """.trimIndent()
        val questao = QuestaoMapper.fromDto(dtoBase(alternativas = raw))

        assertEquals(2, questao.alternativas.size)
        assertEquals("A", questao.alternativas[0].letra)
        assertEquals("B", questao.alternativas[1].letra)
    }

    @Test
    fun `fromDto injeta C e E para modalidade CERTO_ERRADO sem alternativas`() {
        val questao = QuestaoMapper.fromDto(
            dtoBase(modalidade = "CERTO_ERRADO", alternativas = "", gabarito = "C")
        )

        assertEquals(2, questao.alternativas.size)
        assertEquals("C", questao.alternativas[0].letra)
        assertEquals("Certo", questao.alternativas[0].texto)
        assertEquals("E", questao.alternativas[1].letra)
        assertEquals("Errado", questao.alternativas[1].texto)
    }

    @Test
    fun `fromDto nao injeta alternativas em questao anulada`() {
        val questao = QuestaoMapper.fromDto(
            dtoBase(modalidade = "CERTO_ERRADO", alternativas = "", gabarito = "X")
        )

        assertTrue(questao.isAnulada)
        assertEquals(0, questao.alternativas.size)
    }

    @Test
    fun `fromDto nao injeta C_E quando alternativas ja existem em CERTO_ERRADO`() {
        val raw = "A) Certo\nB) Errado"
        val questao = QuestaoMapper.fromDto(
            dtoBase(modalidade = "CERTO_ERRADO", alternativas = raw)
        )

        assertEquals(2, questao.alternativas.size)
        assertEquals("A", questao.alternativas[0].letra)
    }

    @Test
    fun `catalogoFromDto mapeia id e nome`() {
        val dto = br.com.mauricio.oconcurseiro.data.remote.CatalogoItemDto(id = 42L, nome = "Direito Constitucional")
        val item = QuestaoMapper.catalogoFromDto(dto)

        assertEquals(42L, item.id)
        assertEquals("Direito Constitucional", item.nome)
    }
}
