package co.edu.udistrital.udfy.security;

import java.util.Map;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Aspect
@Component
public class RolDbAspect {

    static final Map<String, String> ROLES_BD = Map.of(
            "OYENTE", "rol_oyente",
            "ARTISTA", "rol_artista",
            "ANALISTA", "rol_analista",
            "ADMIN", "rol_admin");

    private final JdbcTemplate jdbcTemplate;

    public RolDbAspect(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Around("""
            within(co.edu.udistrital.udfy.service.*)
            && !within(co.edu.udistrital.udfy.service.AuthService)
            && execution(* *(..))
            && (@annotation(org.springframework.transaction.annotation.Transactional)
                || @within(org.springframework.transaction.annotation.Transactional))
            """)
    public Object aplicarRol(ProceedingJoinPoint joinPoint) throws Throwable {
        jdbcTemplate.execute("SET LOCAL ROLE " + rolBdDelJwt());
        return joinPoint.proceed();
    }

    String rolBdDelJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Autenticación inválida");
        }
        Object rolClaim = jwt.getClaim("rol");
        String rolBd = rolClaim == null ? null : ROLES_BD.get(String.valueOf(rolClaim));
        if (rolBd == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Rol no reconocido");
        }
        return rolBd;
    }
}
