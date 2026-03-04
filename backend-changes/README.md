# Backend Changes - Comentários

Copy the entire `comentario` package to the backend project:

```
backend-changes/src/main/java/br/com/concurseiro/api/comentario/
  -> concurseiro-api/src/main/java/br/com/concurseiro/api/comentario/
```

Files:
- `Comentario.java` - JPA entity (table auto-created by hibernate ddl-auto=update)
- `ComentarioRepository.java` - Spring Data JPA repository
- `ComentarioRequest.java` - Request DTO (record)
- `ComentarioResponse.java` - Response DTO (record)
- `ComentarioController.java` - REST controller with endpoints:
  - `GET /api/v1/questoes/{questaoId}/comentarios` - List comments (paginated, sorted by curtidas or recentes)
  - `POST /api/v1/questoes/{questaoId}/comentarios` - Create comment
  - `POST /api/v1/comentarios/{id}/curtir` - Like a comment
  - `POST /api/v1/comentarios/{id}/descurtir` - Dislike a comment

All responses are automatically wrapped by `ApiResponseEnvelopeAdvice` in the standard `{"success": true, "data": ...}` format.

No SQL migration needed - JPA creates the `comentarios` table automatically.
