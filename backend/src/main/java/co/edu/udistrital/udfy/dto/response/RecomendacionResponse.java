package co.edu.udistrital.udfy.dto.response;

public record RecomendacionResponse(
        Long idCancion,
        String titulo,
        String youtubeId,
        Integer duracionSegundos,
        String nombreArtista,
        String nombreGenero
) {
}
