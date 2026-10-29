package co.edu.udistrital.udfy.controller;

import co.edu.udistrital.udfy.dto.request.CambiarRolCuentaRequest;
import co.edu.udistrital.udfy.dto.request.CrearCuentaRequest;
import co.edu.udistrital.udfy.dto.response.CuentaCreadaResponse;
import co.edu.udistrital.udfy.dto.response.CuentaResponse;
import co.edu.udistrital.udfy.service.CuentaService;
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
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {
    private final CuentaService cuentaService;

    public CuentaController(CuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }

    @PostMapping
    public ResponseEntity<CuentaCreadaResponse> crear(@Valid @RequestBody CrearCuentaRequest request) {
        CuentaCreadaResponse response = cuentaService.crear(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<CuentaResponse>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(cuentaService.listar(page, size));
    }

    @PutMapping("/{idCuenta}/rol")
    public ResponseEntity<CuentaResponse> cambiarRol(@PathVariable Long idCuenta,
                                                     @Valid @RequestBody CambiarRolCuentaRequest request,
                                                     Authentication authentication) {
        Long idCuentaSolicitante = extraerIdCuenta(authentication);
        return ResponseEntity.ok(cuentaService.cambiarRol(idCuenta, idCuentaSolicitante, request));
    }

    private Long extraerIdCuenta(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String subject = jwt.getSubject();
        if (subject == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Token sin claim sub");
        }
        return Long.valueOf(subject);
    }
}
