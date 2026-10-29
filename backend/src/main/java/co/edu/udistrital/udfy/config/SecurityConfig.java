package co.edu.udistrital.udfy.config;

import co.edu.udistrital.udfy.security.JwtRoleConverter;
import co.edu.udistrital.udfy.security.jwt.JwtService;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtService jwtService;

    public SecurityConfig(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new JwtRoleConverter());

        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/health", "/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/generos").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/generos").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/generos/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/artistas/me/top-canciones").hasRole("ARTISTA")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/artistas", "/api/artistas/**").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/artistas/me").hasRole("ARTISTA")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/usuarios/me").hasRole("OYENTE")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/usuarios/me").hasRole("OYENTE")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/reproducciones").hasRole("OYENTE")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/reproducciones/me").hasRole("OYENTE")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/recomendaciones/me").hasRole("OYENTE")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/artistas/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/canciones").hasRole("ARTISTA")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/canciones/**").hasRole("ARTISTA")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/canciones/**").authenticated()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt
                        .jwtAuthenticationConverter(jwtAuthenticationConverter)
                        .decoder(jwtDecoder())
                ));
        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKeySpec key = new SecretKeySpec(jwtService.getSecretKey(), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
    }
}
