package br.com.mauricio.oconcurseiro.data.local

import android.content.Context

class GuestUsageManager(context: Context) {

    private val prefs = context.getSharedPreferences("guest_usage", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_DATE = "current_date"
        private const val KEY_COUNT = "question_count"
        private const val DAILY_LIMIT = 5
    }

    private fun hoje(): String {
        val formato = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return formato.format(java.util.Date())
    }

    private fun sincronizarDia() {
        val hoje = hoje()
        val dataSalva = prefs.getString(KEY_DATE, null)

        if (dataSalva != hoje) {
            prefs.edit()
                .putString(KEY_DATE, hoje)
                .putInt(KEY_COUNT, 0)
                .apply()
        }
    }

    fun podeResolverSemLogin(): Boolean {
        sincronizarDia()
        return prefs.getInt(KEY_COUNT, 0) < DAILY_LIMIT
    }

    fun registrarResolucao() {
        sincronizarDia()
        val atual = prefs.getInt(KEY_COUNT, 0)
        prefs.edit().putInt(KEY_COUNT, atual + 1).apply()
    }

    fun resolucoesRestantes(): Int {
        sincronizarDia()
        val usadas = prefs.getInt(KEY_COUNT, 0)
        return (DAILY_LIMIT - usadas).coerceAtLeast(0)
    }
}