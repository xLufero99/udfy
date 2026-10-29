package co.edu.udistrital.udfy.dto.response;

import java.time.OffsetDateTime;

public record CuentaResponse(
        Long idCuenta,
        String email,
        String rol,
        Long idUsuario,
        Long idArtista,
        OffsetDateTime fechaCreacion
) {
}
