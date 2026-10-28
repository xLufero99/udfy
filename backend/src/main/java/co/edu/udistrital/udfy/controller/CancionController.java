package co.edu.udistrital.udfy.controller;

import co.edu.udistrital.udfy.dto.request.ActualizarCancionRequest;
import co.edu.udistrital.udfy.dto.request.CancionRequest;
import co.edu.udistrital.udfy.dto.response.CancionResponse;
import co.edu.udistrital.udfy.service.CancionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/canciones")
public class CancionController {
    private final CancionService cancionService;

    public CancionController(CancionService cancionService) {
        this.cancionService = cancionService;
    }

    @PostMapping
    public ResponseEntity<CancionResponse> crear(@Valid @RequestBody CancionRequest request, Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Object idArtistaClaim = jwt.getClaim("id_artista");
        if (idArtistaClaim == null) {
            throw new IllegalArgumentException("No autorizado");
        }
        Long idArtista = Long.valueOf(String.valueOf(idArtistaClaim));
        CancionResponse response = cancionService.crear(idArtista, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{idCancion}")
    public ResponseEntity<CancionResponse> editar(@PathVariable Long idCancion, @Valid @RequestBody ActualizarCancionRequest request, Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Object idArtistaClaim = jwt.getClaim("id_artista");
        if (idArtistaClaim == null) {
            throw new IllegalArgumentException("No autorizado");
        }
        Long idArtista = Long.valueOf(String.valueOf(idArtistaClaim));
        return ResponseEntity.ok(cancionService.editar(idArtista, idCancion, request));
    }

    @GetMapping("/artista/{idArtista}")
    public ResponseEntity<Page<CancionResponse>> listarPorArtista(@PathVariable Long idArtista,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(cancionService.listarPorArtista(idArtista, page, size));
    }

    @GetMapping("/genero/{idGenero}")
    public ResponseEntity<Page<CancionResponse>> listarPorGenero(@PathVariable Long idGenero,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(cancionService.listarPorGenero(idGenero, page, size));
    }
}
