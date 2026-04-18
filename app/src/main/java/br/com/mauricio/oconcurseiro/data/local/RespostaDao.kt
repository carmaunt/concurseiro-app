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
            WHERE respondidaEm >= :desde AND usuarioId = :usuarioId
            GROUP BY questaoId
        )
    """)
    suspend fun totalRespostasDesde(usuarioId: String, desde: Long): Int

    @Query("""
        SELECT COUNT(*) FROM respostas r
        INNER JOIN (
            SELECT questaoId, MAX(respondidaEm) AS ultimaResposta
            FROM respostas
            WHERE respondidaEm >= :desde AND usuarioId = :usuarioId
            GROUP BY questaoId
        ) ult
        ON r.questaoId = ult.questaoId AND r.respondidaEm = ult.ultimaResposta
        WHERE r.acertou = 1 AND r.usuarioId = :usuarioId
    """)
    suspend fun totalAcertosDesde(usuarioId: String, desde: Long): Int

    @Query("""
        SELECT COUNT(*) FROM respostas r
        INNER JOIN (
            SELECT questaoId, MAX(respondidaEm) AS ultimaResposta
            FROM respostas
            WHERE usuarioId = :usuarioId
            GROUP BY questaoId
        ) ult
        ON r.questaoId = ult.questaoId AND r.respondidaEm = ult.ultimaResposta
        WHERE r.acertou = 0 AND r.respondidaEm >= :desde AND r.usuarioId = :usuarioId
    """)
    suspend fun totalErrosDesde(usuarioId: String, desde: Long): Int

    @Query("""
        SELECT COUNT(*) FROM (
            SELECT questaoId
            FROM respostas
            WHERE usuarioId = :usuarioId
            GROUP BY questaoId
        )
    """)
    suspend fun totalRespostas(usuarioId: String): Int

    @Query("""
        SELECT COUNT(*) FROM respostas r
        INNER JOIN (
            SELECT questaoId, MAX(respondidaEm) AS ultimaResposta
            FROM respostas
            WHERE usuarioId = :usuarioId
            GROUP BY questaoId
        ) ult
        ON r.questaoId = ult.questaoId AND r.respondidaEm = ult.ultimaResposta
        WHERE r.acertou = 1 AND r.usuarioId = :usuarioId
    """)
    suspend fun totalAcertos(usuarioId: String): Int

    @Query("""
        SELECT * FROM respostas
        WHERE usuarioId = :usuarioId
        ORDER BY respondidaEm DESC
        LIMIT :limit
    """)
    suspend fun ultimasRespostas(usuarioId: String, limit: Int = 20): List<RespostaEntity>

    @Query("""
        SELECT * FROM respostas
        WHERE questaoId = :questaoId AND usuarioId = :usuarioId
        ORDER BY respondidaEm DESC
        LIMIT 1
    """)
    suspend fun ultimaRespostaPorQuestao(usuarioId: String, questaoId: String): RespostaEntity?
}