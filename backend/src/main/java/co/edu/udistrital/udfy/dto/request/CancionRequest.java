package co.edu.udistrital.udfy.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CancionRequest(
        @NotBlank String titulo,
        @NotNull Long idGenero,
        @NotNull Integer duracionSegundos,
        @NotBlank String youtubeLink,
        @NotNull LocalDate fechaLanzamiento
) {
}
