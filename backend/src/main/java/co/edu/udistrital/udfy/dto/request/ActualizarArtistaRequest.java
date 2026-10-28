package co.edu.udistrital.udfy.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ActualizarArtistaRequest(
        @NotBlank String nombre,
        @NotBlank String pais,
        @NotNull @Min(1) @Max(119) Integer edad,
        @NotNull Long idGenero
) {
}
