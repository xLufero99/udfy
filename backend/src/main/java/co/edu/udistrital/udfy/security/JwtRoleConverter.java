package co.edu.udistrital.udfy.security;

import java.util.Collection;
import java.util.Collections;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

public class JwtRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Object rol = jwt.getClaim("rol");
        if (rol == null) {
            return Collections.emptyList();
        }
        String role = String.valueOf(rol).toUpperCase();
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    }
}
