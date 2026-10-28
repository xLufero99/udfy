package co.edu.udistrital.udfy.service;

import co.edu.udistrital.udfy.dto.request.LoginRequest;
import co.edu.udistrital.udfy.dto.request.RegistroArtistaRequest;
import co.edu.udistrital.udfy.dto.request.RegistroOyenteRequest;
import co.edu.udistrital.udfy.dto.response.LoginResponse;
import co.edu.udistrital.udfy.dto.response.RegistroResponse;
import co.edu.udistrital.udfy.model.entity.Artista;
import co.edu.udistrital.udfy.model.entity.Cuenta;
import co.edu.udistrital.udfy.model.entity.Usuario;
import co.edu.udistrital.udfy.repository.ArtistaRepository;
import co.edu.udistrital.udfy.repository.CuentaRepository;
import co.edu.udistrital.udfy.repository.UsuarioRepository;
import co.edu.udistrital.udfy.security.jwt.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final CuentaRepository cuentaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ArtistaRepository artistaRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(CuentaRepository cuentaRepository, UsuarioRepository usuarioRepository, ArtistaRepository artistaRepository, JwtService jwtService) {
        this.cuentaRepository = cuentaRepository;
        this.usuarioRepository = usuarioRepository;
        this.artistaRepository = artistaRepository;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        String email = request.email().toLowerCase();
        Cuenta cuenta = cuentaRepository.findByEmail(email).orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        if (!passwordEncoder.matches(request.password(), cuenta.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        String token = jwtService.generateToken(cuenta.getIdCuenta(), cuenta.getRol(), cuenta.getIdUsuario(), cuenta.getIdArtista());
        return new LoginResponse(token);
    }

    @Transactional
    public RegistroResponse registrarOyente(RegistroOyenteRequest request) {
        String email = request.email().toLowerCase();
        if (cuentaRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre());
        usuario.setPais(request.pais());
        usuario.setEdad(request.edad());
        usuario = usuarioRepository.save(usuario);

        Cuenta cuenta = new Cuenta();
        cuenta.setEmail(email);
        cuenta.setPasswordHash(passwordEncoder.encode(request.password()));
        cuenta.setRol("OYENTE");
        cuenta.setIdUsuario(usuario.getIdUsuario());
        cuenta.setIdArtista(null);
        cuenta = cuentaRepository.save(cuenta);
        return new RegistroResponse("Oyente registered", cuenta.getIdCuenta());
    }

    @Transactional
    public RegistroResponse registrarArtista(RegistroArtistaRequest request) {
        String email = request.email().toLowerCase();
        if (cuentaRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        Artista artista = new Artista();
        artista.setNombre(request.nombre());
        artista.setPais(request.pais());
        artista.setEdad(request.edad());
        artista.setIdGenero(request.idGenero());
        artista = artistaRepository.save(artista);

        Cuenta cuenta = new Cuenta();
        cuenta.setEmail(email);
        cuenta.setPasswordHash(passwordEncoder.encode(request.password()));
        cuenta.setRol("ARTISTA");
        cuenta.setIdUsuario(null);
        cuenta.setIdArtista(artista.getIdArtista());
        cuenta = cuentaRepository.save(cuenta);
        return new RegistroResponse("Artista registered", cuenta.getIdCuenta());
    }
}
