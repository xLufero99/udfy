package co.edu.udistrital.udfy.controller;

import co.edu.udistrital.udfy.dto.request.ActualizarArtistaRequest;
import co.edu.udistrital.udfy.dto.response.ArtistaResponse;
import co.edu.udistrital.udfy.dto.response.TopCancionResponse;
import co.edu.udistrital.udfy.service.ArtistaService;
import co.edu.udistrital.udfy.service.TopCancionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/artistas")
public class ArtistaController {
    private final ArtistaService artistaService;
    private final TopCancionService topCancionService;

    public ArtistaController(ArtistaService artistaService, TopCancionService topCancionService) {
        this.artistaService = artistaService;
        this.topCancionService = topCancionService;
    }

    @GetMapping
    public ResponseEntity<List<ArtistaResponse>> listar() {
        return ResponseEntity.ok(artistaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtistaResponse> detalle(@PathVariable Long id) {
        return ResponseEntity.ok(artistaService.detalle(id));
    }

    @PutMapping("/me")
    public ResponseEntity<ArtistaResponse> editarPerfil(@Valid @RequestBody ActualizarArtistaRequest request, Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Object idArtistaClaim = jwt.getClaim("id_artista");
        if (idArtistaClaim == null) {
            throw new IllegalArgumentException("No autorizado");
        }
        Long idArtista = Long.valueOf(String.valueOf(idArtistaClaim));
        return ResponseEntity.ok(artistaService.editarPerfil(idArtista, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        artistaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/top-canciones")
    public ResponseEntity<List<TopCancionResponse>> topCanciones(
            @RequestParam(defaultValue = "10") int limit,
            Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Object idArtistaClaim = jwt.getClaim("id_artista");
        if (idArtistaClaim == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Token sin claim id_artista");
        }
        Long idArtista = Long.valueOf(String.valueOf(idArtistaClaim));
        return ResponseEntity.ok(topCancionService.topCanciones(idArtista, limit));
    }
}
