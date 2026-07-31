package br.com.mauricio.oconcurseiro.ads

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InterstitialFrequencyPolicyTest {
    @Test
    fun `nao exibe antes de oito respostas`() {
        assertFalse(
            InterstitialFrequencyPolicy.isEligible(
                answeredQuestions = 7,
                lastShownAtMillis = 0L,
                nowMillis = 1_000L
            )
        )
    }

    @Test
    fun `exibe apos oito respostas quando nunca houve anuncio`() {
        assertTrue(
            InterstitialFrequencyPolicy.isEligible(
                answeredQuestions = 8,
                lastShownAtMillis = 0L,
                nowMillis = 1_000L
            )
        )
    }

    @Test
    fun `respeita intervalo minimo de dez minutos`() {
        val lastShownAt = 1_000L

        assertFalse(
            InterstitialFrequencyPolicy.isEligible(
                answeredQuestions = 12,
                lastShownAtMillis = lastShownAt,
                nowMillis = lastShownAt + 9 * 60 * 1_000L
            )
        )
        assertTrue(
            InterstitialFrequencyPolicy.isEligible(
                answeredQuestions = 12,
                lastShownAtMillis = lastShownAt,
                nowMillis = lastShownAt + 10 * 60 * 1_000L
            )
        )
    }
}
