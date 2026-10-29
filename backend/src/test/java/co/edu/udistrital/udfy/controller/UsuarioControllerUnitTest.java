package co.edu.udistrital.udfy.controller;

import co.edu.udistrital.udfy.dto.request.ActualizarUsuarioRequest;
import co.edu.udistrital.udfy.dto.response.UsuarioResponse;
import co.edu.udistrital.udfy.service.UsuarioService;
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
class UsuarioControllerUnitTest {
    @Mock
    private UsuarioService usuarioService;
    @InjectMocks
    private UsuarioController usuarioController;

    private Authentication autenticacionConClaim(String claim, Object valor) {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim(claim)).thenReturn(valor);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);
        return authentication;
    }

    @Test
    void obtenerPerfilTomaElIdDelJwt() {
        Authentication authentication = autenticacionConClaim("id_usuario", 7);
        when(usuarioService.obtenerPerfil(7L)).thenReturn(new UsuarioResponse(7L, "Ana", "Colombia", 20, null));

        ResponseEntity<UsuarioResponse> response = usuarioController.obtenerPerfil(authentication);

        assertEquals(200, response.getStatusCode().value());
        verify(usuarioService).obtenerPerfil(7L);
    }

    @Test
    void editarPerfilTomaElIdDelJwt() {
        Authentication authentication = autenticacionConClaim("id_usuario", 7);
        ActualizarUsuarioRequest request = new ActualizarUsuarioRequest("Ana", "Colombia", 20);
        when(usuarioService.editarPerfil(7L, request)).thenReturn(new UsuarioResponse(7L, "Ana", "Colombia", 20, null));

        ResponseEntity<UsuarioResponse> response = usuarioController.editarPerfil(request, authentication);

        assertEquals(200, response.getStatusCode().value());
        verify(usuarioService).editarPerfil(7L, request);
    }

    @Test
    void sinClaimIdUsuarioResponde403() {
        Authentication authentication = autenticacionConClaim("id_usuario", null);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> usuarioController.obtenerPerfil(authentication));
        assertEquals(403, ex.getStatusCode().value());
    }
}
