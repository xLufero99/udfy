package co.edu.udistrital.udfy.controller;

import co.edu.udistrital.udfy.dto.request.ActualizarUsuarioRequest;
import co.edu.udistrital.udfy.dto.response.UsuarioResponse;
import co.edu.udistrital.udfy.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> obtenerPerfil(Authentication authentication) {
        Long idUsuario = extraerIdUsuario(authentication);
        return ResponseEntity.ok(usuarioService.obtenerPerfil(idUsuario));
    }

    @PutMapping("/me")
    public ResponseEntity<UsuarioResponse> editarPerfil(@Valid @RequestBody ActualizarUsuarioRequest request,
                                                        Authentication authentication) {
        Long idUsuario = extraerIdUsuario(authentication);
        return ResponseEntity.ok(usuarioService.editarPerfil(idUsuario, request));
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
