package co.edu.udistrital.udfy.service;

import co.edu.udistrital.udfy.dto.request.CambiarRolCuentaRequest;
import co.edu.udistrital.udfy.dto.request.CrearCuentaRequest;
import co.edu.udistrital.udfy.dto.response.CuentaCreadaResponse;
import co.edu.udistrital.udfy.dto.response.CuentaResponse;
import co.edu.udistrital.udfy.model.entity.Cuenta;
import co.edu.udistrital.udfy.repository.CuentaRepository;
import co.edu.udistrital.udfy.security.util.PasswordEncoderUtil;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CuentaService {
    static final Set<String> ROLES_GESTIONABLES = Set.of("ANALISTA", "ADMIN");

    private final CuentaRepository cuentaRepository;
    private final PasswordEncoder passwordEncoder;

    public CuentaService(CuentaRepository cuentaRepository, PasswordEncoderUtil passwordEncoderUtil) {
        this.cuentaRepository = cuentaRepository;
        this.passwordEncoder = passwordEncoderUtil.getPasswordEncoder();
    }

    @Transactional
    public CuentaCreadaResponse crear(CrearCuentaRequest request) {
        if (!ROLES_GESTIONABLES.contains(request.rol())) {
            throw new IllegalArgumentException("Solo se pueden crear cuentas con rol ANALISTA o ADMIN");
        }
        Cuenta cuenta = new Cuenta();
        cuenta.setEmail(request.email().toLowerCase());
        cuenta.setPasswordHash(passwordEncoder.encode(request.password()));
        cuenta.setRol(request.rol());
        cuenta.setIdUsuario(null);
        cuenta.setIdArtista(null);
        cuenta = cuentaRepository.save(cuenta);
        return new CuentaCreadaResponse("Cuenta creada", cuenta.getIdCuenta());
    }

    public Page<CuentaResponse> listar(int page, int size) {
        int pageSize = size > 50 ? 50 : (size < 1 ? 20 : size);
        int pageNumber = page < 0 ? 0 : page;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "idCuenta"));
        return cuentaRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional
    public CuentaResponse cambiarRol(Long idCuenta, Long idCuentaSolicitante, CambiarRolCuentaRequest request) {
        if (!ROLES_GESTIONABLES.contains(request.rol())) {
            throw new IllegalArgumentException("Solo se pueden asignar los roles ANALISTA o ADMIN");
        }
        Cuenta cuenta = cuentaRepository.findById(idCuenta)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada"));
        if (!ROLES_GESTIONABLES.contains(cuenta.getRol())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La cuenta solo puede alternar entre los roles ANALISTA y ADMIN");
        }
        if (idCuenta.equals(idCuentaSolicitante)) {
            throw new IllegalArgumentException("Un administrador no puede cambiar su propio rol");
        }
        cuenta.setRol(request.rol());
        cuenta = cuentaRepository.save(cuenta);
        return toResponse(cuenta);
    }

    private CuentaResponse toResponse(Cuenta cuenta) {
        return new CuentaResponse(
                cuenta.getIdCuenta(),
                cuenta.getEmail(),
                cuenta.getRol(),
                cuenta.getIdUsuario(),
                cuenta.getIdArtista(),
                cuenta.getFechaCreacion()
        );
    }
}
