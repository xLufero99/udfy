package co.edu.udistrital.udfy.service;

import co.edu.udistrital.udfy.dto.request.RegistrarReproduccionRequest;
import co.edu.udistrital.udfy.dto.response.ReproduccionCreadaResponse;
import co.edu.udistrital.udfy.model.entity.Reproduccion;
import co.edu.udistrital.udfy.repository.ReproduccionRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReproduccionServiceUnitTest {
    @Mock
    private ReproduccionRepository reproduccionRepository;
    @InjectMocks
    private ReproduccionService reproduccionService;

    @Test
    void registrarUsaIdDelJwtYNoEnviaFecha() {
        when(reproduccionRepository.save(any(Reproduccion.class))).thenAnswer(inv -> inv.getArgument(0));
        RegistrarReproduccionRequest request = new RegistrarReproduccionRequest(3L, new BigDecimal("12.50"), true);

        ReproduccionCreadaResponse response = reproduccionService.registrar(7L, request);

        ArgumentCaptor<Reproduccion> captor = ArgumentCaptor.forClass(Reproduccion.class);
        verify(reproduccionRepository).save(captor.capture());
        Reproduccion guardada = captor.getValue();
        assertEquals(7L, guardada.getIdUsuario());
        assertEquals(3L, guardada.getIdCancion());
        assertEquals(new BigDecimal("12.50"), guardada.getMinutosReproducidos());
        assertEquals(Boolean.TRUE, guardada.getCompletada());
        assertNull(guardada.getFechaReproduccion());
        assertEquals("Reproducción registrada", response.message());
    }

    @Test
    void registrarPropagaErrorDeIntegridadDeLaBD() {
        when(reproduccionRepository.save(any(Reproduccion.class)))
                .thenThrow(new DataIntegrityViolationException("constraint"));
        RegistrarReproduccionRequest request = new RegistrarReproduccionRequest(3L, new BigDecimal("-1"), false);
        assertThrows(DataIntegrityViolationException.class, () -> reproduccionService.registrar(7L, request));
    }

    @Test
    void historialAplicaLimiteMaximoDe50() {
        when(reproduccionRepository.findByUsuarioConDetalle(eq(7L), any(Pageable.class))).thenReturn(Page.empty());
        reproduccionService.historial(7L, 0, 100);
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(reproduccionRepository).findByUsuarioConDetalle(eq(7L), captor.capture());
        assertEquals(50, captor.getValue().getPageSize());
    }

    @Test
    void historialUsa20CuandoElTamanioEsInvalido() {
        when(reproduccionRepository.findByUsuarioConDetalle(eq(7L), any(Pageable.class))).thenReturn(Page.empty());
        reproduccionService.historial(7L, -1, 0);
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(reproduccionRepository).findByUsuarioConDetalle(eq(7L), captor.capture());
        Pageable pageable = captor.getValue();
        assertEquals(0, pageable.getPageNumber());
        assertEquals(20, pageable.getPageSize());
        assertEquals(Sort.Direction.DESC, pageable.getSort().getOrderFor("fechaReproduccion").getDirection());
        assertEquals(Sort.Direction.DESC, pageable.getSort().getOrderFor("idReproduccion").getDirection());
    }

    @Test
    void historialUsaElTamanoSolicitadoCuandoEsValido() {
        when(reproduccionRepository.findByUsuarioConDetalle(eq(7L), any(Pageable.class))).thenReturn(Page.empty());
        reproduccionService.historial(7L, 1, 30);
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(reproduccionRepository).findByUsuarioConDetalle(eq(7L), captor.capture());
        assertEquals(1, captor.getValue().getPageNumber());
        assertEquals(30, captor.getValue().getPageSize());
    }
}
