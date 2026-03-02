package br.com.mauricio.oconcurseiro.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

data class DesempenhoStats(
    val total: Int,
    val acertos: Int,
    val erros: Int
)

@Dao
interface RespostaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(resposta: RespostaEntity)

    @Query("SELECT COUNT(*) FROM respostas WHERE respondidaEm >= :desde")
    suspend fun totalRespostasDesde(desde: Long): Int

    @Query("SELECT COUNT(*) FROM respostas WHERE acertou = 1 AND respondidaEm >= :desde")
    suspend fun totalAcertosDesde(desde: Long): Int

    @Query("SELECT COUNT(*) FROM respostas WHERE acertou = 0 AND respondidaEm >= :desde")
    suspend fun totalErrosDesde(desde: Long): Int

    @Query("SELECT COUNT(*) FROM respostas")
    suspend fun totalRespostas(): Int

    @Query("SELECT COUNT(*) FROM respostas WHERE acertou = 1")
    suspend fun totalAcertos(): Int

    @Query("SELECT * FROM respostas ORDER BY respondidaEm DESC LIMIT :limit")
    suspend fun ultimasRespostas(limit: Int = 20): List<RespostaEntity>

    @Query("SELECT COUNT(*) FROM respostas WHERE questaoId = :questaoId")
    suspend fun jaRespondeu(questaoId: String): Int

    @Query("SELECT * FROM respostas WHERE questaoId = :questaoId ORDER BY respondidaEm DESC LIMIT 1")
    suspend fun ultimaRespostaPorQuestao(questaoId: String): RespostaEntity?
}
