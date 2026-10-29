package co.edu.udistrital.udfy.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CambiarRolCuentaRequest(
        @NotBlank String rol
) {
}
