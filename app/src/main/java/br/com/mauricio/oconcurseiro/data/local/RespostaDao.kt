package br.com.mauricio.oconcurseiro.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RespostaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(resposta: RespostaEntity)

    @Query("""
        SELECT COUNT(*) FROM (
            SELECT questaoId, MAX(respondidaEm) AS ultimaResposta
            FROM respostas
            WHERE respondidaEm >= :desde
            GROUP BY questaoId
        )
    """)
    suspend fun totalRespostasDesde(desde: Long): Int

    @Query("""
        SELECT COUNT(*) FROM respostas r
        INNER JOIN (
            SELECT questaoId, MAX(respondidaEm) AS ultimaResposta
            FROM respostas
            WHERE respondidaEm >= :desde
            GROUP BY questaoId
        ) ult
        ON r.questaoId = ult.questaoId AND r.respondidaEm = ult.ultimaResposta
        WHERE r.acertou = 1
    """)
    suspend fun totalAcertosDesde(desde: Long): Int

    @Query("""
        SELECT COUNT(*) FROM respostas r
        INNER JOIN (
            SELECT questaoId, MAX(respondidaEm) AS ultimaResposta
            FROM respostas
            GROUP BY questaoId
        ) ult
        ON r.questaoId = ult.questaoId AND r.respondidaEm = ult.ultimaResposta
        WHERE r.acertou = 0 AND r.respondidaEm >= :desde
    """)
    suspend fun totalErrosDesde(desde: Long): Int

    @Query("""
        SELECT COUNT(*) FROM (
            SELECT questaoId
            FROM respostas
            GROUP BY questaoId
        )
    """)
    suspend fun totalRespostas(): Int

    @Query("""
        SELECT COUNT(*) FROM respostas r
        INNER JOIN (
            SELECT questaoId, MAX(respondidaEm) AS ultimaResposta
            FROM respostas
            GROUP BY questaoId
        ) ult
        ON r.questaoId = ult.questaoId AND r.respondidaEm = ult.ultimaResposta
        WHERE r.acertou = 1
    """)
    suspend fun totalAcertos(): Int

    @Query("""
        SELECT * FROM respostas
        ORDER BY respondidaEm DESC
        LIMIT :limit
    """)
    suspend fun ultimasRespostas(limit: Int = 20): List<RespostaEntity>

    @Query("SELECT COUNT(*) FROM respostas WHERE questaoId = :questaoId")
    suspend fun jaRespondeu(questaoId: String): Int

    @Query("""
        SELECT * FROM respostas
        WHERE questaoId = :questaoId
        ORDER BY respondidaEm DESC
        LIMIT 1
    """)
    suspend fun ultimaRespostaPorQuestao(questaoId: String): RespostaEntity?
}