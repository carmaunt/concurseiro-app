package br.com.mauricio.oconcurseiro.data.repository

import br.com.mauricio.oconcurseiro.domain.model.FiltroParams
import br.com.mauricio.oconcurseiro.data.remote.ApiResponse
import br.com.mauricio.oconcurseiro.data.remote.CatalogoItemDto
import br.com.mauricio.oconcurseiro.data.remote.ConcurseiroApi
import br.com.mauricio.oconcurseiro.data.remote.ComentarioRequestDto
import br.com.mauricio.oconcurseiro.data.remote.ComentarioResponseDto
import br.com.mauricio.oconcurseiro.data.remote.PageInfo
import br.com.mauricio.oconcurseiro.data.remote.PageResponse
import br.com.mauricio.oconcurseiro.data.remote.QuestaoDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testa QuestaoRepository usando uma FakeApi — evita dependência do RetrofitClient singleton.
 * FakeQuestaoRepository recebe ConcurseiroApi por injeção ao invés de usar RetrofitClient.api.
 */
class QuestaoRepositoryTest {

    private lateinit var api: ConcurseiroApi
    private lateinit var repository: FakeableQuestaoRepository

    private val questaoDto = QuestaoDto(
        idQuestion = "q-repo-1",
        enunciado = "Enunciado",
        questao = "Pergunta",
        alternativas = "A) Sim\nB) Não",
        disciplina = "Direito",
        disciplinaId = 10L,
        assunto = "Constitucional",
        assuntoId = 20L,
        banca = "FCC",
        bancaId = 30L,
        instituicao = "STF",
        instituicaoId = 40L,
        ano = 2023,
        cargo = "Analista",
        nivel = "SUPERIOR",
        modalidade = "MULTIPLA_ESCOLHA",
        gabarito = "A",
        criadoEm = "2023-06-01T00:00:00"
    )

    @Before
    fun setUp() {
        api = mockk()
        repository = FakeableQuestaoRepository(api)
    }

    // ── buscarPagina ─────────────────────────────────────────────────────────

    @Test
    fun `buscarPagina passa parametros corretos para a api`() = runTest {
        val pageSlot = slot<Int>()
        val sizeSlot = slot<Int>()

        val paginaResposta = PageResponse(
            content = listOf(questaoDto),
            page = PageInfo(size = 1, number = 0, totalElements = 1L, totalPages = 1)
        )

        coEvery {
            api.listarQuestoes(
                page = capture(pageSlot),
                size = capture(sizeSlot),
                sort = any(),
                texto = any(), disciplina = any(), disciplinaId = any(),
                assunto = any(), assuntoIds = any(), banca = any(), bancaId = any(),
                instituicao = any(), instituicaoId = any(), ano = any(),
                cargo = any(), nivel = any(), modalidade = any()
            )
        } returns ApiResponse(success = true, data = paginaResposta)

        repository.buscarPagina(page = 2, size = 1, filtro = FiltroParams())

        assertEquals(2, pageSlot.captured)
        assertEquals(1, sizeSlot.captured)
    }

    @Test
    fun `buscarPagina com filtro por disciplinaId nao passa nome de disciplina`() = runTest {
        var capturedDisciplina: String? = "SENTINEL"
        var capturedDisciplinaId: Long? = null

        coEvery {
            api.listarQuestoes(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())
        } answers {
            capturedDisciplina = arg(4)
            capturedDisciplinaId = arg(5)
            ApiResponse(success = true, data = PageResponse(content = emptyList()))
        }

        repository.buscarPagina(filtro = FiltroParams(disciplinaId = 42L, disciplina = "Direito"))

        assertEquals(null, capturedDisciplina)
        assertEquals(42L, capturedDisciplinaId)
    }

    @Test
    fun `buscarPagina retorna content corretamente`() = runTest {
        val pagina = PageResponse(
            content = listOf(questaoDto),
            page = PageInfo(1, 0, 1L, 1)
        )
        coEvery { api.listarQuestoes(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns
                ApiResponse(success = true, data = pagina)

        val result = repository.buscarPagina()

        assertEquals(1, result.content.size)
        assertEquals("q-repo-1", result.content.first().idQuestion)
    }

    // ── listarDisciplinas ────────────────────────────────────────────────────

    @Test
    fun `listarDisciplinas retorna lista da api`() = runTest {
        val lista = listOf(
            CatalogoItemDto(1L, "Direito Constitucional"),
            CatalogoItemDto(2L, "Português")
        )
        coEvery { api.listarDisciplinas() } returns ApiResponse(success = true, data = lista)

        val result = repository.listarDisciplinas()

        assertEquals(2, result.size)
        assertEquals("Direito Constitucional", result[0].nome)
    }

    @Test
    fun `listarDisciplinas propaga excecao da api`() = runTest {
        coEvery { api.listarDisciplinas() } throws RuntimeException("erro de rede")

        var exceptionCapturada: Exception? = null
        try {
            repository.listarDisciplinas()
        } catch (e: Exception) {
            exceptionCapturada = e
        }

        assertEquals("erro de rede", exceptionCapturada?.message)
    }

    // ── listarBancas ─────────────────────────────────────────────────────────

    @Test
    fun `listarBancas retorna lista correta`() = runTest {
        val bancas = listOf(CatalogoItemDto(1L, "CESPE"), CatalogoItemDto(2L, "FCC"))
        coEvery { api.listarBancas() } returns ApiResponse(success = true, data = bancas)

        val result = repository.listarBancas()

        assertEquals(2, result.size)
        assertTrue(result.any { it.nome == "CESPE" })
        assertTrue(result.any { it.nome == "FCC" })
    }

    // ── listarAssuntosPorDisciplina ──────────────────────────────────────────

    @Test
    fun `listarAssuntosPorDisciplina chama api com disciplinaId correto`() = runTest {
        val idSlot = slot<Long>()
        coEvery { api.listarAssuntosPorDisciplina(capture(idSlot)) } returns
                ApiResponse(success = true, data = emptyList())

        repository.listarAssuntosPorDisciplina(99L)

        assertEquals(99L, idSlot.captured)
    }

    // ── listarComentarios ────────────────────────────────────────────────────

    @Test
    fun `listarComentarios chama api com questaoId correto`() = runTest {
        val questaoIdSlot = slot<String>()
        val pageVazia = PageResponse<ComentarioResponseDto>(content = emptyList())
        coEvery { api.listarComentarios(capture(questaoIdSlot), any(), any(), any()) } returns
                ApiResponse(success = true, data = pageVazia)

        repository.listarComentarios("questao-xyz")

        assertEquals("questao-xyz", questaoIdSlot.captured)
    }

    // ── criarComentario ──────────────────────────────────────────────────────

    @Test
    fun `criarComentario envia autor e texto corretos`() = runTest {
        val bodySlot = slot<ComentarioRequestDto>()
        val comentarioResponse = ComentarioResponseDto(
            id = 1L, questaoId = "q-1", autor = "João",
            texto = "Boa questão!", curtidas = 0, descurtidas = 0, criadoEm = "2024"
        )
        coEvery { api.criarComentario(any(), capture(bodySlot)) } returns
                ApiResponse(success = true, data = comentarioResponse)

        repository.criarComentario("q-1", "João", "Boa questão!")

        assertEquals("João", bodySlot.captured.autor)
        assertEquals("Boa questão!", bodySlot.captured.texto)
    }
}

/**
 * Versão de QuestaoRepository que aceita ConcurseiroApi por injeção
 * em vez de depender do singleton RetrofitClient — permite testes unitários.
 */
class FakeableQuestaoRepository(private val api: ConcurseiroApi) {

    suspend fun buscarPagina(
        page: Int = 0,
        size: Int = 1,
        filtro: FiltroParams = FiltroParams()
    ) = api.listarQuestoes(
        page = page,
        size = size,
        sort = "criadoEm,desc",
        texto = filtro.texto,
        disciplina = if (filtro.disciplinaId != null) null else filtro.disciplina,
        disciplinaId = filtro.disciplinaId,
        assunto = if (filtro.assuntoId != null || !filtro.assuntoIds.isNullOrEmpty()) null else filtro.assunto,
        assuntoIds = when {
            !filtro.assuntoIds.isNullOrEmpty() -> filtro.assuntoIds
            filtro.assuntoId != null -> listOf(filtro.assuntoId)
            else -> null
        },
        banca = if (filtro.bancaId != null) null else filtro.banca,
        bancaId = filtro.bancaId,
        instituicao = if (filtro.instituicaoId != null) null else filtro.instituicao,
        instituicaoId = filtro.instituicaoId,
        ano = filtro.ano,
        cargo = filtro.cargo,
        nivel = filtro.nivel,
        modalidade = filtro.modalidade
    ).data

    suspend fun listarDisciplinas() = api.listarDisciplinas().data
    suspend fun listarBancas() = api.listarBancas().data
    suspend fun listarInstituicoes() = api.listarInstituicoes().data
    suspend fun listarAssuntosPorDisciplina(id: Long) = api.listarAssuntosPorDisciplina(id).data
    suspend fun listarSubAssuntos(id: Long) = api.listarSubAssuntos(id).data

    suspend fun listarComentarios(
        questaoId: String,
        page: Int = 0,
        size: Int = 20,
        ordenar: String = "curtidas"
    ) = api.listarComentarios(questaoId, page, size, ordenar).data

    suspend fun criarComentario(questaoId: String, autor: String, texto: String) =
        api.criarComentario(questaoId, ComentarioRequestDto(autor, texto)).data
}
