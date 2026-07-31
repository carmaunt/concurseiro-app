package br.com.mauricio.oconcurseiro.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class StudyStreakCalculatorTest {

    @Test
    fun `preserva sequencia de ontem enquanto meta de hoje esta pendente`() {
        val streak = StudyStreakCalculator.currentStreak(
            listOf(false, true, true, true, false)
        )

        assertEquals(3, streak)
    }

    @Test
    fun `inclui hoje quando a meta foi concluida`() {
        val streak = StudyStreakCalculator.currentStreak(
            listOf(true, true, true, false)
        )

        assertEquals(3, streak)
    }

    @Test
    fun `retorna zero quando ontem tambem nao foi concluido`() {
        val streak = StudyStreakCalculator.currentStreak(
            listOf(false, false, true)
        )

        assertEquals(0, streak)
    }
}
