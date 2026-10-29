package co.edu.udistrital.udfy.dto.request;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record RegistrarReproduccionRequest(
        @NotNull Long idCancion,
        @NotNull BigDecimal minutosReproducidos,
        @NotNull Boolean completada
) {
}
