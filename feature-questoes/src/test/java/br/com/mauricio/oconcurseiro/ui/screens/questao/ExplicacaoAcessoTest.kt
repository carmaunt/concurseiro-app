package br.com.mauricio.oconcurseiro.ui.screens.questao

import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExplicacaoAcessoTest {

    @Test
    fun `ativa somente para usuario logado que resolveu agora e possui explicacao`() {
        assertTrue(
            podeAtivarExplicacao(
                resolveuAgora = true,
                usuarioAutenticado = true
            )
        )
    }

    @Test
    fun `permanece inativa para visitante mesmo depois de resolver`() {
        assertFalse(
            podeAtivarExplicacao(
                resolveuAgora = true,
                usuarioAutenticado = false
            )
        )
    }

    @Test
    fun `permanece inativa antes da resolucao para usuario logado`() {
        assertFalse(
            podeAtivarExplicacao(
                resolveuAgora = false,
                usuarioAutenticado = true
            )
        )
    }

    @Test
    fun `ativa mesmo quando o backend ainda nao possui explicacao`() {
        assertEquals(
            "A explicação desta questão ainda não foi cadastrada.",
            textoExplicacaoOuIndisponivel(null)
        )
    }
}
