package co.edu.udistrital.udfy.dto.response;

public record ReproduccionPorCancionResponse(
        Long idCancion,
        String titulo,
        String nombreArtista,
        String nombreGenero,
        Long totalReproducciones
) {
}
