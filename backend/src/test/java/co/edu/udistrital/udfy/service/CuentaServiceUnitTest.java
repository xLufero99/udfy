package co.edu.udistrital.udfy.service;

import co.edu.udistrital.udfy.dto.request.CambiarRolCuentaRequest;
import co.edu.udistrital.udfy.dto.request.CrearCuentaRequest;
import co.edu.udistrital.udfy.dto.response.CuentaCreadaResponse;
import co.edu.udistrital.udfy.dto.response.CuentaResponse;
import co.edu.udistrital.udfy.model.entity.Cuenta;
import co.edu.udistrital.udfy.repository.CuentaRepository;
import co.edu.udistrital.udfy.security.util.PasswordEncoderUtil;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CuentaServiceUnitTest {
    @Mock
    private CuentaRepository cuentaRepository;
    @Mock
    private PasswordEncoderUtil passwordEncoderUtil;

    private CuentaService construirServicio(PasswordEncoder encoder) {
        when(passwordEncoderUtil.getPasswordEncoder()).thenReturn(encoder);
        return new CuentaService(cuentaRepository, passwordEncoderUtil);
    }

    private CuentaService construirServicioSinEncoder() {
        return new CuentaService(cuentaRepository, passwordEncoderUtil);
    }

    @Test
    void crearConRolNoPermitidoLanza400() {
        CuentaService cuentaService = construirServicioSinEncoder();
        CrearCuentaRequest request = new CrearCuentaRequest("ana@ud.edu.co", "secreta123", "OYENTE");
        assertThrows(IllegalArgumentException.class, () -> cuentaService.crear(request));
    }

    @Test
    void crearUsaEmailEnMinusculasYHashBcrypt() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        CuentaService cuentaService = construirServicio(encoder);
        when(cuentaRepository.save(any(Cuenta.class))).thenAnswer(inv -> inv.getArgument(0));
        CrearCuentaRequest request = new CrearCuentaRequest("ANA@UD.EDU.CO", "secreta123", "ANALISTA");

        CuentaCreadaResponse response = cuentaService.crear(request);

        ArgumentCaptor<Cuenta> captor = ArgumentCaptor.forClass(Cuenta.class);
        verify(cuentaRepository).save(captor.capture());
        Cuenta guardada = captor.getValue();
        assertEquals("ana@ud.edu.co", guardada.getEmail());
        assertEquals("ANALISTA", guardada.getRol());
        assertNotEquals("secreta123", guardada.getPasswordHash());
        assertTrue(encoder.matches("secreta123", guardada.getPasswordHash()));
        assertEquals("Cuenta creada", response.message());
    }

    @Test
    void crearDuplicadoPropagaErrorDeIntegridad() {
        CuentaService cuentaService = construirServicio(new BCryptPasswordEncoder());
        when(cuentaRepository.save(any(Cuenta.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate"));
        CrearCuentaRequest request = new CrearCuentaRequest("ana@ud.edu.co", "secreta123", "ADMIN");
        assertThrows(DataIntegrityViolationException.class, () -> cuentaService.crear(request));
    }

    @Test
    void listarNoExponePasswordHash() {
        CuentaService cuentaService = construirServicioSinEncoder();
        Cuenta cuenta = new Cuenta();
        cuenta.setEmail("ana@ud.edu.co");
        cuenta.setPasswordHash("$2a$10$hashsecreto");
        cuenta.setRol("ANALISTA");
        when(cuentaRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(cuenta)));

        Page<CuentaResponse> page = cuentaService.listar(0, 20);

        CuentaResponse response = page.getContent().get(0);
        assertEquals(6, response.getClass().getRecordComponents().length);
        assertEquals("ana@ud.edu.co", response.email());
        assertEquals("ANALISTA", response.rol());
    }

    @Test
    void cambiarRolAOyenteOArtistaResponde409() {
        CuentaService cuentaService = construirServicioSinEncoder();
        Cuenta cuenta = new Cuenta();
        cuenta.setEmail("oyente@ud.edu.co");
        cuenta.setRol("OYENTE");
        when(cuentaRepository.findById(5L)).thenReturn(Optional.of(cuenta));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> cuentaService.cambiarRol(5L, 1L, new CambiarRolCuentaRequest("ADMIN")));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void adminNoPuedeCambiarSuPropioRol() {
        CuentaService cuentaService = construirServicioSinEncoder();
        Cuenta cuenta = new Cuenta();
        cuenta.setEmail("admin@ud.edu.co");
        cuenta.setRol("ADMIN");
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));

        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.cambiarRol(1L, 1L, new CambiarRolCuentaRequest("ANALISTA")));
    }

    @Test
    void cambiarRolEntreAnalistaYAdminFunciona() {
        CuentaService cuentaService = construirServicioSinEncoder();
        Cuenta cuenta = new Cuenta();
        cuenta.setEmail("ana@ud.edu.co");
        cuenta.setRol("ANALISTA");
        when(cuentaRepository.findById(2L)).thenReturn(Optional.of(cuenta));
        when(cuentaRepository.save(any(Cuenta.class))).thenAnswer(inv -> inv.getArgument(0));

        CuentaResponse response = cuentaService.cambiarRol(2L, 1L, new CambiarRolCuentaRequest("ADMIN"));

        assertEquals("ADMIN", response.rol());
        assertEquals("ana@ud.edu.co", response.email());
    }

    @Test
    void cambiarRolANoGestionableLanza400() {
        CuentaService cuentaService = construirServicioSinEncoder();
        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.cambiarRol(2L, 1L, new CambiarRolCuentaRequest("OYENTE")));
    }
}
