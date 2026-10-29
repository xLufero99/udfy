package co.edu.udistrital.udfy.controller;

import co.edu.udistrital.udfy.dto.response.RecomendacionResponse;
import co.edu.udistrital.udfy.service.RecomendacionService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/recomendaciones")
public class RecomendacionController {
    private final RecomendacionService recomendacionService;

    public RecomendacionController(RecomendacionService recomendacionService) {
        this.recomendacionService = recomendacionService;
    }

    @GetMapping("/me")
    public ResponseEntity<List<RecomendacionResponse>> recomendaciones(
            @RequestParam(defaultValue = "10") int limit,
            Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Object idUsuarioClaim = jwt.getClaim("id_usuario");
        if (idUsuarioClaim == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Token sin claim id_usuario");
        }
        Long idUsuario = Long.valueOf(String.valueOf(idUsuarioClaim));
        return ResponseEntity.ok(recomendacionService.recomendaciones(idUsuario, limit));
    }
}
