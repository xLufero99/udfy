package co.edu.udistrital.udfy.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record HistorialReproduccionResponse(
        Long idReproduccion,
        BigDecimal minutosReproducidos,
        Boolean completada,
        OffsetDateTime fechaReproduccion,
        Long idCancion,
        String titulo,
        String nombreArtista,
        String nombreGenero
) {
}
