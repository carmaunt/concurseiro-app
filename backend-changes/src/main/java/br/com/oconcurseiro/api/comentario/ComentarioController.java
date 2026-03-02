package br.com.oconcurseiro.api.comentario;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ComentarioController {

    private final ComentarioRepository comentarioRepository;

    public ComentarioController(ComentarioRepository comentarioRepository) {
        this.comentarioRepository = comentarioRepository;
    }

    @GetMapping("/questoes/{questaoId}/comentarios")
    public ResponseEntity<Page<ComentarioResponseDto>> listarComentarios(
            @PathVariable String questaoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "curtidas") String ordenar
    ) {
        Sort sort;
        if ("recentes".equals(ordenar)) {
            sort = Sort.by(Sort.Direction.DESC, "criadoEm");
        } else {
            sort = Sort.by(Sort.Direction.DESC, "curtidas");
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Comentario> comentarios = comentarioRepository.findByQuestaoId(questaoId, pageable);
        Page<ComentarioResponseDto> response = comentarios.map(ComentarioResponseDto::fromEntity);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/questoes/{questaoId}/comentarios")
    public ResponseEntity<ComentarioResponseDto> criarComentario(
            @PathVariable String questaoId,
            @Valid @RequestBody ComentarioRequestDto request
    ) {
        Comentario comentario = new Comentario(questaoId, request.getAutor(), request.getTexto());
        Comentario salvo = comentarioRepository.save(comentario);
        return ResponseEntity.status(HttpStatus.CREATED).body(ComentarioResponseDto.fromEntity(salvo));
    }

    @PostMapping("/comentarios/{id}/curtir")
    public ResponseEntity<ComentarioResponseDto> curtir(@PathVariable Long id) {
        return comentarioRepository.findById(id)
                .map(comentario -> {
                    comentario.setCurtidas(comentario.getCurtidas() + 1);
                    Comentario salvo = comentarioRepository.save(comentario);
                    return ResponseEntity.ok(ComentarioResponseDto.fromEntity(salvo));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/comentarios/{id}/descurtir")
    public ResponseEntity<ComentarioResponseDto> descurtir(@PathVariable Long id) {
        return comentarioRepository.findById(id)
                .map(comentario -> {
                    comentario.setDescurtidas(comentario.getDescurtidas() + 1);
                    Comentario salvo = comentarioRepository.save(comentario);
                    return ResponseEntity.ok(ComentarioResponseDto.fromEntity(salvo));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
