package br.com.mauricio.oconcurseiro.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "respostas")
data class RespostaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val usuarioId: String,
    val questaoId: String,
    val disciplina: String,
    val acertou: Boolean,
    val respostaSelecionada: String,
    val gabarito: String,
    val respondidaEm: Long = System.currentTimeMillis()
)