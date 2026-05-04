package br.com.mauricio.oconcurseiro.ui.viewmodel

import br.com.mauricio.oconcurseiro.data.auth.AuthRepository
import br.com.mauricio.oconcurseiro.data.local.RespostaDao
import br.com.mauricio.oconcurseiro.data.remote.CatalogoItemDto
import br.com.mauricio.oconcurseiro.data.repository.QuestaoRepository
import br.com.mauricio.oconcurseiro.domain.model.Alternativa
import br.com.mauricio.oconcurseiro.domain.model.FiltroParams
import br.com.mauricio.oconcurseiro.domain.model.PaginaResultado
import br.com.mauricio.oconcurseiro.domain.model.Questao
import br.com.mauricio.oconcurseiro.domain.usecase.BuscarPaginaQuestoesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.net.UnknownHostException

@OptIn(ExperimentalCoroutinesApi::class)
class QuestaoViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var buscarPaginaQuestoesUseCase: BuscarPaginaQuestoesUseCase
    private lateinit var repository: QuestaoRepository
    private lateinit var respostaDao: RespostaDao
    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: QuestaoViewModel

    private val questao = Questao(
        id = "q-test-1",
        disciplina = "Raciocínio Lógico",
        disciplinaId = 1L,
        assunto = "Lógica",
        assuntoId = 2L,
        ano = 2024,
        banca = "CESPE",
        bancaId = 3L,
        orgao = "TCU",
        orgaoId = 4L,
        cargo = "Auditor",
        nivel = "SUPERIOR",
        modalidade = "MULTIPLA_ESCOLHA",
        enunciado = "Enunciado de teste",
        questao = "Qual é a resposta correta?",
        gabarito = "B",
        alternativas = listOf(
            Alternativa(letra = "A", texto = "Alfa"),
            Alternativa(letra = "B", texto = "Beta"),
            Alternativa(letra = "C", texto = "Gama"),
            Alternativa(letra = "D", texto = "Delta"),
            Alternativa(letra = "E", texto = "Épsilon")
        )
    )

    private val paginaComUmaQuestao = PaginaResultado(
        content = listOf(questao),
        number = 0,
        size = 1,
        totalElements = 10L,
        totalPages = 10,
        first = true,
        last = false
    )

    private val paginaVazia = PaginaResultado<Questao>(
        content = emptyList(),
        number = 0,
        size = 1,
        totalElements = 0L,
        totalPages = 0,
        first = true,
        last = true
    )

    private val catalogoVazio = emptyList<CatalogoItemDto>()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        buscarPaginaQuestoesUseCase = mockk()
        repository = mockk()
        respostaDao = mockk(relaxed = true)
        authRepository = mockk()

        every { authRepository.estaAutenticado() } returns false
        every { authRepository.usuarioIdOuGuest() } returns "guest-id"

        coEvery { repository.listarDisciplinas() } returns catalogoVazio
        coEvery { repository.listarBancas() } returns catalogoVazio
        coEvery { repository.listarInstituicoes() } returns catalogoVazio
        coEvery { respostaDao.ultimaRespostaPorQuestao(any(), any()) } returns null

        viewModel = QuestaoViewModel(
            buscarPaginaQuestoesUseCase = buscarPaginaQuestoesUseCase,
            repository = repository,
            respostaDao = respostaDao,
            authRepository = authRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `carregarQuestao com sucesso popula uiState questao`() = runTest {
        coEvery { buscarPaginaQuestoesUseCase(any(), any(), any()) } returns paginaComUmaQuestao

        viewModel.carregarQuestao()
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.questao)
        assertEquals("q-test-1", viewModel.uiState.questao?.id)
        assertFalse(viewModel.uiState.isLoading)
        assertNull(viewModel.uiState.erro)
        assertFalse(viewModel.uiState.isEmpty)
    }

    @Test
    fun `carregarQuestao com resultado vazio seta isEmpty`() = runTest {
        coEvery { buscarPaginaQuestoesUseCase(any(), any(), any()) } returns paginaVazia

        viewModel.carregarQuestao()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.isEmpty)
        assertNull(viewModel.uiState.questao)
        assertNull(viewModel.uiState.erro)
        assertFalse(viewModel.uiState.isLoading)
    }

    @Test
    fun `carregarQuestao com excecao de rede seta mensagem de erro`() = runTest {
        coEvery { buscarPaginaQuestoesUseCase(any(), any(), any()) } throws UnknownHostException()

        viewModel.carregarQuestao()
        advanceUntilIdle()

        assertEquals("Sem conexão com a internet", viewModel.uiState.erro)
        assertNull(viewModel.uiState.questao)
        assertFalse(viewModel.uiState.isLoading)
    }

    @Test
    fun `carregarQuestao persiste filtro aplicado`() = runTest {
        val filtro = FiltroParams(disciplina = "Direito", ano = 2022)
        coEvery { buscarPaginaQuestoesUseCase(any(), any(), any()) } returns paginaVazia

        viewModel.carregarQuestao(filtro)
        advanceUntilIdle()

        assertEquals("Direito", viewModel.filtroAtual.disciplina)
        assertEquals(2022, viewModel.filtroAtual.ano)
    }

    @Test
    fun `carregarQuestao seta totalQuestoes corretamente`() = runTest {
        coEvery { buscarPaginaQuestoesUseCase(any(), any(), any()) } returns paginaComUmaQuestao

        viewModel.carregarQuestao()
        advanceUntilIdle()

        assertEquals(10, viewModel.uiState.totalQuestoes)
    }

    @Test
    fun `proxima incrementa pagina e recarrega`() = runTest {
        coEvery { buscarPaginaQuestoesUseCase(any(), any(), any()) } returns paginaComUmaQuestao

        viewModel.carregarQuestao()
        advanceUntilIdle()

        viewModel.proxima()
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.paginaAtual)
    }

    @Test
    fun `proxima nao avanca quando na ultima pagina`() = runTest {
        val paginaFinal = PaginaResultado(
            content = listOf(questao),
            number = 0,
            size = 1,
            totalElements = 1L,
            totalPages = 1,
            first = true,
            last = true
        )

        coEvery { buscarPaginaQuestoesUseCase(any(), any(), any()) } returns paginaFinal

        viewModel.carregarQuestao()
        advanceUntilIdle()

        val paginaAntes = viewModel.uiState.paginaAtual

        viewModel.proxima()
        advanceUntilIdle()

        assertEquals(paginaAntes, viewModel.uiState.paginaAtual)
    }

    @Test
    fun `anterior decrementa pagina`() = runTest {
        coEvery { buscarPaginaQuestoesUseCase(any(), any(), any()) } returns paginaComUmaQuestao

        viewModel.carregarQuestao()
        advanceUntilIdle()

        viewModel.proxima()
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.paginaAtual)

        viewModel.anterior()
        advanceUntilIdle()

        assertEquals(0, viewModel.uiState.paginaAtual)
    }

    @Test
    fun `anterior nao decrementa abaixo de zero`() = runTest {
        coEvery { buscarPaginaQuestoesUseCase(any(), any(), any()) } returns paginaComUmaQuestao

        viewModel.carregarQuestao()
        advanceUntilIdle()

        assertEquals(0, viewModel.uiState.paginaAtual)

        viewModel.anterior()
        advanceUntilIdle()

        assertEquals(0, viewModel.uiState.paginaAtual)
    }

    @Test
    fun `aplicarFiltro reseta pagina para zero`() = runTest {
        coEvery { buscarPaginaQuestoesUseCase(any(), any(), any()) } returns paginaComUmaQuestao

        viewModel.carregarQuestao()
        advanceUntilIdle()

        viewModel.proxima()
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.paginaAtual)

        viewModel.aplicarFiltro(FiltroParams(disciplina = "Matemática"))
        advanceUntilIdle()

        assertEquals(0, viewModel.uiState.paginaAtual)
    }

    @Test
    fun `salvarResposta chama respostaDao inserir`() = runTest {
        viewModel.salvarResposta(
            questaoId = "q-test-1",
            disciplina = "Raciocínio Lógico",
            respostaSelecionada = "B",
            gabarito = "B",
            acertou = true
        )
        advanceUntilIdle()

        coVerify { respostaDao.inserir(any()) }
    }

    @Test
    fun `salvarResposta com questao ja respondida nao exibe banner na proxima visita`() = runTest {
        coEvery { buscarPaginaQuestoesUseCase(any(), any(), any()) } returns paginaComUmaQuestao

        viewModel.salvarResposta("q-test-1", "Lógica", "B", "B", true)
        viewModel.carregarQuestao()
        advanceUntilIdle()

        assertNull(viewModel.uiState.respostaAnterior)
    }

    @Test
    fun `recarregar chama carregarQuestao com filtro atual`() = runTest {
        val filtro = FiltroParams(disciplina = "Português")
        coEvery { buscarPaginaQuestoesUseCase(any(), any(), any()) } returns paginaVazia

        viewModel.carregarQuestao(filtro)
        advanceUntilIdle()

        viewModel.recarregar()
        advanceUntilIdle()

        assertEquals("Português", viewModel.filtroAtual.disciplina)
    }
}