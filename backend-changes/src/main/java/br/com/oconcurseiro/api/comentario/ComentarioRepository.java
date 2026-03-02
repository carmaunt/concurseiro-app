package br.com.oconcurseiro.api.comentario;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    Page<Comentario> findByQuestaoId(String questaoId, Pageable pageable);

    long countByQuestaoId(String questaoId);
}
