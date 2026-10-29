package co.edu.udistrital.udfy.dto.response;

import java.math.BigDecimal;

public record MinutosPorOyenteResponse(
        Long idUsuario,
        String nombre,
        BigDecimal totalMinutos
) {
}
