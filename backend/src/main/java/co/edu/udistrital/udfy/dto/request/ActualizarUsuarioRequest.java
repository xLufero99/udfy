package co.edu.udistrital.udfy.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ActualizarUsuarioRequest(
        @NotBlank String nombre,
        @NotBlank String pais,
        @NotNull Integer edad
) {
}
