package co.edu.udistrital.udfy.controller;

import co.edu.udistrital.udfy.dto.request.RegistrarReproduccionRequest;
import co.edu.udistrital.udfy.dto.response.HistorialReproduccionResponse;
import co.edu.udistrital.udfy.dto.response.ReproduccionCreadaResponse;
import co.edu.udistrital.udfy.service.ReproduccionService;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
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
class ReproduccionControllerUnitTest {
    @Mock
    private ReproduccionService reproduccionService;
    @InjectMocks
    private ReproduccionController reproduccionController;

    private Authentication autenticacionConClaim(Object valor) {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("id_usuario")).thenReturn(valor);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);
        return authentication;
    }

    @Test
    void registrarTomaElIdDelJwtYResponde201() {
        Authentication authentication = autenticacionConClaim(7);
        RegistrarReproduccionRequest request = new RegistrarReproduccionRequest(3L, new BigDecimal("10"), true);
        when(reproduccionService.registrar(7L, request)).thenReturn(new ReproduccionCreadaResponse("Reproducción registrada", 1L));

        ResponseEntity<ReproduccionCreadaResponse> response = reproduccionController.registrar(request, authentication);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1L, response.getBody().idReproduccion());
        verify(reproduccionService).registrar(7L, request);
    }

    @Test
    void registrarSinClaimIdUsuarioResponde403() {
        Authentication authentication = autenticacionConClaim(null);
        RegistrarReproduccionRequest request = new RegistrarReproduccionRequest(3L, new BigDecimal("10"), true);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> reproduccionController.registrar(request, authentication));
        assertEquals(403, ex.getStatusCode().value());
    }

    @Test
    void historialTomaElIdDelJwt() {
        Authentication authentication = autenticacionConClaim(7);
        when(reproduccionService.historial(7L, 0, 20)).thenReturn(Page.empty());

        ResponseEntity<Page<HistorialReproduccionResponse>> response = reproduccionController.historial(0, 20, authentication);

        assertEquals(200, response.getStatusCode().value());
        verify(reproduccionService).historial(7L, 0, 20);
    }

    @Test
    void historialSinClaimIdUsuarioResponde403() {
        Authentication authentication = autenticacionConClaim(null);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> reproduccionController.historial(0, 20, authentication));
        assertEquals(403, ex.getStatusCode().value());
    }
}
