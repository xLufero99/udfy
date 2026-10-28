package co.edu.udistrital.udfy.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GeneroRequest(
        @NotBlank String nombre
) {
}
