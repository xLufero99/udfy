package co.edu.udistrital.udfy.dto.response;

import java.time.OffsetDateTime;

public record UsuarioResponse(
        Long idUsuario,
        String nombre,
        String pais,
        Integer edad,
        OffsetDateTime fechaRegistro
) {
}
