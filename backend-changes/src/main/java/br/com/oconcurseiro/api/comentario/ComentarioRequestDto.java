package br.com.oconcurseiro.api.comentario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ComentarioRequestDto {

    @NotBlank(message = "O nome do autor é obrigatório")
    @Size(max = 100, message = "O nome do autor deve ter no máximo 100 caracteres")
    private String autor;

    @NotBlank(message = "O texto do comentário é obrigatório")
    @Size(max = 5000, message = "O comentário deve ter no máximo 5000 caracteres")
    private String texto;

    public ComentarioRequestDto() {}

    public ComentarioRequestDto(String autor, String texto) {
        this.autor = autor;
        this.texto = texto;
    }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
}
