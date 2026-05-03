package br.com.mauricio.oconcurseiro.data.local

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testa a lógica pura de limite de visitante isolada de SharedPreferences.
 * GuestLimitEngine encapsula as regras sem depender de Context.
 */
class GuestLimitTest {

    companion object {
        private const val LIMITE_DIARIO = 5
    }

    private lateinit var engine: GuestLimitEngine

    @Before
    fun setUp() {
        engine = GuestLimitEngine(limiteDisario = LIMITE_DIARIO)
    }

    @Test
    fun `visitante pode resolver quando nenhuma questao foi respondida`() {
        assertTrue(engine.podeResolverSemLogin())
    }

    @Test
    fun `visitante pode resolver questoes ate o limite`() {
        repeat(LIMITE_DIARIO - 1) { i ->
            engine.registrarResolucao("q-$i")
            assertTrue("Deveria poder resolver questao $i", engine.podeResolverSemLogin())
        }
    }

    @Test
    fun `visitante nao pode resolver apos atingir limite`() {
        repeat(LIMITE_DIARIO) { i -> engine.registrarResolucao("q-$i") }
        assertFalse(engine.podeResolverSemLogin())
    }

    @Test
    fun `podeResolverQuestao retorna true para questao ja respondida mesmo no limite`() {
        repeat(LIMITE_DIARIO) { i -> engine.registrarResolucao("q-$i") }
        assertTrue(engine.podeResolverQuestao("q-0"))
    }

    @Test
    fun `podeResolverQuestao retorna false para questao nova apos limite`() {
        repeat(LIMITE_DIARIO) { i -> engine.registrarResolucao("q-$i") }
        assertFalse(engine.podeResolverQuestao("q-nova"))
    }

    @Test
    fun `resolucoesRestantes decrementa a cada registro`() {
        assertEquals(LIMITE_DIARIO, engine.resolucoesRestantes())
        engine.registrarResolucao("q-1")
        assertEquals(LIMITE_DIARIO - 1, engine.resolucoesRestantes())
        engine.registrarResolucao("q-2")
        assertEquals(LIMITE_DIARIO - 2, engine.resolucoesRestantes())
    }

    @Test
    fun `resolucoesRestantes nao vai abaixo de zero`() {
        repeat(LIMITE_DIARIO + 3) { i -> engine.registrarResolucao("q-$i") }
        assertEquals(0, engine.resolucoesRestantes())
    }

    @Test
    fun `registrar mesma questao duas vezes nao consome duas slots`() {
        engine.registrarResolucao("q-duplicada")
        engine.registrarResolucao("q-duplicada")
        assertEquals(LIMITE_DIARIO - 1, engine.resolucoesRestantes())
    }

    @Test
    fun `jaContabilizouHoje retorna true para questao registrada`() {
        engine.registrarResolucao("q-abc")
        assertTrue(engine.jaContabilizouHoje("q-abc"))
    }

    @Test
    fun `jaContabilizouHoje retorna false para questao nao registrada`() {
        assertFalse(engine.jaContabilizouHoje("q-desconhecida"))
    }

    @Test
    fun `resetar zera todas as resolucoes`() {
        repeat(3) { i -> engine.registrarResolucao("q-$i") }
        engine.resetar()
        assertEquals(LIMITE_DIARIO, engine.resolucoesRestantes())
        assertTrue(engine.podeResolverSemLogin())
    }
}

/**
 * Engine de limite de visitante sem dependência de Context/SharedPreferences.
 * Espelha a lógica de GuestUsageManager para ser testável em JVM pura.
 */
class GuestLimitEngine(private val limiteDisario: Int = 5) {

    private val questoesResolvidas = mutableSetOf<String>()

    fun podeResolverSemLogin(): Boolean = questoesResolvidas.size < limiteDisario

    fun registrarResolucao(questaoId: String) {
        questoesResolvidas.add(questaoId)
    }

    fun jaContabilizouHoje(questaoId: String): Boolean = questaoId in questoesResolvidas

    fun podeResolverQuestao(questaoId: String): Boolean =
        questaoId in questoesResolvidas || questoesResolvidas.size < limiteDisario

    fun resolucoesRestantes(): Int =
        (limiteDisario - questoesResolvidas.size).coerceAtLeast(0)

    fun resetar() = questoesResolvidas.clear()
}
