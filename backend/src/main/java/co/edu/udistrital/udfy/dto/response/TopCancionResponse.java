package co.edu.udistrital.udfy.dto.response;

public record TopCancionResponse(
        Long idCancion,
        String titulo,
        String youtubeId,
        Integer duracionSegundos,
        String nombreGenero,
        Long totalReproducciones
) {
}
