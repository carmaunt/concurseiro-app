package br.com.mauricio.oconcurseiro.util

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

fun formatarData(dataStr: String): String {
    return try {
        val cleaned = dataStr
            .replace(Regex("\\+\\d{2}:\\d{2}$"), "")
            .replace(Regex("-\\d{2}:\\d{2}$"), "")
            .replace("Z", "")
            .substringBefore(".")
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale("pt", "BR"))
        parser.timeZone = TimeZone.getTimeZone("America/Sao_Paulo")
        val date = parser.parse(cleaned) ?: return dataStr
        val formatter = SimpleDateFormat("dd 'de' MMMM 'de' yyyy, HH:mm", Locale("pt", "BR"))
        formatter.format(date)
    } catch (_: Exception) {
        dataStr
    }
}
