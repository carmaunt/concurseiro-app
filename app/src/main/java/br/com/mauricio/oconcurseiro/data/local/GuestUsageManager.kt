package br.com.mauricio.oconcurseiro.data.local

import android.content.Context
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GuestUsageManager(context: Context) {

    private val prefs = context.getSharedPreferences("guest_usage", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_DATE = "current_date"
        private const val KEY_QUESTIONS = "resolved_question_ids"
        private const val DAILY_LIMIT = 5
    }

    private fun hoje(): String {
        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formato.format(Date())
    }

    private fun sincronizarDia() {
        val hoje = hoje()
        val dataSalva = prefs.getString(KEY_DATE, null)

        if (dataSalva != hoje) {
            prefs.edit()
                .putString(KEY_DATE, hoje)
                .putString(KEY_QUESTIONS, "[]")
                .apply()
        }
    }

    private fun obterQuestoesResolvidasHoje(): MutableSet<String> {
        sincronizarDia()
        val json = prefs.getString(KEY_QUESTIONS, "[]") ?: "[]"
        val array = JSONArray(json)
        val ids = mutableSetOf<String>()

        for (i in 0 until array.length()) {
            ids.add(array.getString(i))
        }

        return ids
    }

    private fun salvarQuestoesResolvidasHoje(ids: Set<String>) {
        val array = JSONArray()
        ids.forEach { array.put(it) }

        prefs.edit()
            .putString(KEY_QUESTIONS, array.toString())
            .apply()
    }

    fun podeResolverSemLogin(): Boolean {
        val ids = obterQuestoesResolvidasHoje()
        return ids.size < DAILY_LIMIT
    }

    fun registrarResolucao(questaoId: String) {
        val ids = obterQuestoesResolvidasHoje()
        ids.add(questaoId)
        salvarQuestoesResolvidasHoje(ids)
    }

    fun jaContabilizouHoje(questaoId: String): Boolean {
        val ids = obterQuestoesResolvidasHoje()
        return questaoId in ids
    }

    fun resolucoesRestantes(): Int {
        val ids = obterQuestoesResolvidasHoje()
        return (DAILY_LIMIT - ids.size).coerceAtLeast(0)
    }
}