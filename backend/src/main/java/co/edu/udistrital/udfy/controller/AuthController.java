package co.edu.udistrital.udfy.controller;

import co.edu.udistrital.udfy.dto.request.LoginRequest;
import co.edu.udistrital.udfy.dto.request.RegistroArtistaRequest;
import co.edu.udistrital.udfy.dto.request.RegistroOyenteRequest;
import co.edu.udistrital.udfy.dto.response.LoginResponse;
import co.edu.udistrital.udfy.dto.response.RegistroResponse;
import co.edu.udistrital.udfy.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/registro/oyente")
    public ResponseEntity<RegistroResponse> registroOyente(@Valid @RequestBody RegistroOyenteRequest request) {
        return ResponseEntity.ok(authService.registrarOyente(request));
    }

    @PostMapping("/registro/artista")
    public ResponseEntity<RegistroResponse> registroArtista(@Valid @RequestBody RegistroArtistaRequest request) {
        return ResponseEntity.ok(authService.registrarArtista(request));
    }
}
