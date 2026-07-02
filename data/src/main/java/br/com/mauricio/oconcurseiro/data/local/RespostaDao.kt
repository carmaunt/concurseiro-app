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
        SELECT COUNT(*) FROM (
            SELECT questaoId, MAX(respondidaEm) AS ultimaResposta
            FROM respostas
            WHERE respondidaEm >= :inicio AND respondidaEm < :fim AND usuarioId = :usuarioId
            GROUP BY questaoId
        )
    """)
    suspend fun totalRespostasNoPeriodo(usuarioId: String, inicio: Long, fim: Long): Int

    @Query("""
        SELECT * FROM respostas
        WHERE usuarioId = :usuarioId AND respondidaEm >= :inicio AND respondidaEm < :fim
        ORDER BY respondidaEm ASC
    """)
    suspend fun respostasNoPeriodo(usuarioId: String, inicio: Long, fim: Long): List<RespostaEntity>

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

    @Query("""
        SELECT r.disciplina,
               COUNT(*) AS total,
               SUM(CASE WHEN r.acertou = 1 THEN 1 ELSE 0 END) AS acertos
        FROM respostas r
        INNER JOIN (
            SELECT questaoId, MAX(respondidaEm) AS ultimaResposta
            FROM respostas
            WHERE usuarioId = :usuarioId
            GROUP BY questaoId
        ) ult ON r.questaoId = ult.questaoId AND r.respondidaEm = ult.ultimaResposta
        WHERE r.usuarioId = :usuarioId
        GROUP BY r.disciplina
        ORDER BY total DESC
        LIMIT 5
    """)
    suspend fun desempenhoPorDisciplina(usuarioId: String): List<DesempenhoPorDisciplina>

    @Query("""
        SELECT r.disciplina,
               COUNT(*) AS total,
               SUM(CASE WHEN r.acertou = 1 THEN 1 ELSE 0 END) AS acertos
        FROM respostas r
        INNER JOIN (
            SELECT questaoId, MAX(respondidaEm) AS ultimaResposta
            FROM respostas
            WHERE usuarioId = :usuarioId AND respondidaEm >= :desde
            GROUP BY questaoId
        ) ult ON r.questaoId = ult.questaoId AND r.respondidaEm = ult.ultimaResposta
        WHERE r.usuarioId = :usuarioId AND r.respondidaEm >= :desde
        GROUP BY r.disciplina
        ORDER BY total DESC
        LIMIT 5
    """)
    suspend fun desempenhoPorDisciplinaDesde(
        usuarioId: String,
        desde: Long
    ): List<DesempenhoPorDisciplina>
}
