package co.edu.udistrital.udfy.dto.response;

import java.math.BigDecimal;

public record MinutosPorGeneroResponse(
        Long idGenero,
        String nombre,
        BigDecimal totalMinutos
) {
}
