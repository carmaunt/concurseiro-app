package br.com.oconcurseiro.api.comentario;

import java.time.LocalDateTime;

public class ComentarioResponseDto {

    private Long id;
    private String questaoId;
    private String autor;
    private String texto;
    private Integer curtidas;
    private Integer descurtidas;
    private LocalDateTime criadoEm;

    public ComentarioResponseDto() {}

    public static ComentarioResponseDto fromEntity(Comentario comentario) {
        ComentarioResponseDto dto = new ComentarioResponseDto();
        dto.setId(comentario.getId());
        dto.setQuestaoId(comentario.getQuestaoId());
        dto.setAutor(comentario.getAutor());
        dto.setTexto(comentario.getTexto());
        dto.setCurtidas(comentario.getCurtidas());
        dto.setDescurtidas(comentario.getDescurtidas());
        dto.setCriadoEm(comentario.getCriadoEm());
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getQuestaoId() { return questaoId; }
    public void setQuestaoId(String questaoId) { this.questaoId = questaoId; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public Integer getCurtidas() { return curtidas; }
    public void setCurtidas(Integer curtidas) { this.curtidas = curtidas; }

    public Integer getDescurtidas() { return descurtidas; }
    public void setDescurtidas(Integer descurtidas) { this.descurtidas = descurtidas; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
}
