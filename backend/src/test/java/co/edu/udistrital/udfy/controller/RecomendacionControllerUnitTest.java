package co.edu.udistrital.udfy.controller;

import co.edu.udistrital.udfy.service.RecomendacionService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
class RecomendacionControllerUnitTest {
    @Mock
    private RecomendacionService recomendacionService;
    @InjectMocks
    private RecomendacionController recomendacionController;

    private Authentication autenticacionConClaim(Object valor) {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("id_usuario")).thenReturn(valor);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);
        return authentication;
    }

    @Test
    void recomendacionesTomaElIdDelJwt() {
        Authentication authentication = autenticacionConClaim(7);
        when(recomendacionService.recomendaciones(7L, 10)).thenReturn(List.of());

        ResponseEntity<?> response = recomendacionController.recomendaciones(10, authentication);

        assertEquals(200, response.getStatusCode().value());
        verify(recomendacionService).recomendaciones(7L, 10);
    }

    @Test
    void recomendacionesSinClaimIdUsuarioResponde403() {
        Authentication authentication = autenticacionConClaim(null);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> recomendacionController.recomendaciones(10, authentication));
        assertEquals(403, ex.getStatusCode().value());
    }
}
