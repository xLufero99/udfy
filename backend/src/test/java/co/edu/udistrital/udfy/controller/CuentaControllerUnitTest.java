package co.edu.udistrital.udfy.controller;

import co.edu.udistrital.udfy.dto.request.CambiarRolCuentaRequest;
import co.edu.udistrital.udfy.dto.request.CrearCuentaRequest;
import co.edu.udistrital.udfy.dto.response.CuentaCreadaResponse;
import co.edu.udistrital.udfy.dto.response.CuentaResponse;
import co.edu.udistrital.udfy.service.CuentaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CuentaControllerUnitTest {
    @Mock
    private CuentaService cuentaService;
    @InjectMocks
    private CuentaController cuentaController;

    private Authentication autenticacionConSubject(String subject) {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn(subject);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);
        return authentication;
    }

    @Test
    void crearResponde201() {
        CrearCuentaRequest request = new CrearCuentaRequest("ana@ud.edu.co", "secreta123", "ANALISTA");
        when(cuentaService.crear(request)).thenReturn(new CuentaCreadaResponse("Cuenta creada", 9L));

        ResponseEntity<CuentaCreadaResponse> response = cuentaController.crear(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(9L, response.getBody().idCuenta());
    }

    @Test
    void cambiarRolTomaElIdCuentaDelSubDelJwt() {
        Authentication authentication = autenticacionConSubject("1");
        CambiarRolCuentaRequest request = new CambiarRolCuentaRequest("ADMIN");
        when(cuentaService.cambiarRol(2L, 1L, request)).thenReturn(new CuentaResponse(2L, "ana@ud.edu.co", "ADMIN", null, null, null));

        ResponseEntity<CuentaResponse> response = cuentaController.cambiarRol(2L, request, authentication);

        assertEquals(200, response.getStatusCode().value());
        verify(cuentaService).cambiarRol(2L, 1L, request);
    }

    @Test
    void cambiarRolSinSubResponde403() {
        Authentication authentication = autenticacionConSubject(null);
        CambiarRolCuentaRequest request = new CambiarRolCuentaRequest("ADMIN");
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> cuentaController.cambiarRol(2L, request, authentication));
        assertEquals(403, ex.getStatusCode().value());
    }
}
