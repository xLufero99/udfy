package co.edu.udistrital.udfy.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RolDbAspectUnitTest {
    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private ProceedingJoinPoint joinPoint;
    @InjectMocks
    private RolDbAspect rolDbAspect;

    @AfterEach
    void limpiarSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private void autenticar(Object claimRol) {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("rol")).thenReturn(claimRol);
        Authentication authentication = new TestingAuthenticationToken(jwt, null, java.util.List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void mapeaLosCuatroRolesDeLaBD() {
        assertEquals(4, RolDbAspect.ROLES_BD.size());
        assertEquals("rol_oyente", RolDbAspect.ROLES_BD.get("OYENTE"));
        assertEquals("rol_artista", RolDbAspect.ROLES_BD.get("ARTISTA"));
        assertEquals("rol_analista", RolDbAspect.ROLES_BD.get("ANALISTA"));
        assertEquals("rol_admin", RolDbAspect.ROLES_BD.get("ADMIN"));
    }

    @Test
    void ejecutaSetLocalRoleConElRolMapeado() throws Throwable {
        autenticar("OYENTE");
        when(joinPoint.proceed()).thenReturn("ok");

        Object resultado = rolDbAspect.aplicarRol(joinPoint);

        verify(jdbcTemplate).execute("SET LOCAL ROLE rol_oyente");
        assertEquals("ok", resultado);
    }

    @Test
    void rolDesconocidoResponde403SinEjecutar() {
        autenticar("HACKER");
        assertThrows(ResponseStatusException.class, () -> rolDbAspect.aplicarRol(joinPoint));
    }

    @Test
    void claimDeRolFaltanteResponde403() {
        autenticar(null);
        assertThrows(ResponseStatusException.class, () -> rolDbAspect.aplicarRol(joinPoint));
    }

    @Test
    void sinAutenticacionResponde403() {
        assertThrows(ResponseStatusException.class, () -> rolDbAspect.aplicarRol(joinPoint));
    }
}
