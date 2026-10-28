package co.edu.udistrital.udfy.dto.response;

import java.time.LocalDate;

public record CancionResponse(
        Long idCancion,
        String titulo,
        Long idArtista,
        String nombreArtista,
        Long idGenero,
        String nombreGenero,
        Integer duracionSegundos,
        LocalDate fechaLanzamiento,
        String youtubeId
) {
}
