package co.edu.udistrital.udfy.controller;

import co.edu.udistrital.udfy.dto.request.RegistrarReproduccionRequest;
import co.edu.udistrital.udfy.dto.response.HistorialReproduccionResponse;
import co.edu.udistrital.udfy.dto.response.ReproduccionCreadaResponse;
import co.edu.udistrital.udfy.service.ReproduccionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/reproducciones")
public class ReproduccionController {
    private final ReproduccionService reproduccionService;

    public ReproduccionController(ReproduccionService reproduccionService) {
        this.reproduccionService = reproduccionService;
    }

    @PostMapping
    public ResponseEntity<ReproduccionCreadaResponse> registrar(@Valid @RequestBody RegistrarReproduccionRequest request,
                                                                Authentication authentication) {
        Long idUsuario = extraerIdUsuario(authentication);
        ReproduccionCreadaResponse response = reproduccionService.registrar(idUsuario, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/me")
    public ResponseEntity<Page<HistorialReproduccionResponse>> historial(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        Long idUsuario = extraerIdUsuario(authentication);
        return ResponseEntity.ok(reproduccionService.historial(idUsuario, page, size));
    }

    private Long extraerIdUsuario(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Object idUsuarioClaim = jwt.getClaim("id_usuario");
        if (idUsuarioClaim == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Token sin claim id_usuario");
        }
        return Long.valueOf(String.valueOf(idUsuarioClaim));
    }
}
