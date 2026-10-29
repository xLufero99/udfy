package co.edu.udistrital.udfy.service;

import co.edu.udistrital.udfy.repository.ReporteJdbcRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReporteServiceUnitTest {
    @Mock
    private ReporteJdbcRepository reporteJdbcRepository;
    @InjectMocks
    private ReporteService reporteService;

    @Test
    void paginadoAplicaClampDe20A50() {
        when(reporteJdbcRepository.findReproduccionesPorCancion(50, 0)).thenReturn(List.of());
        when(reporteJdbcRepository.countCanciones()).thenReturn(0L);

        Page<?> page = reporteService.reproduccionesPorCancion(0, 100);

        verify(reporteJdbcRepository).findReproduccionesPorCancion(50, 0);
        assertEquals(50, page.getPageable().getPageSize());
    }

    @Test
    void paginadoUsaOffsetSegunLaPagina() {
        when(reporteJdbcRepository.findMinutosPorOyente(20, 40)).thenReturn(List.of());
        when(reporteJdbcRepository.countUsuarios()).thenReturn(100L);

        Page<?> page = reporteService.minutosPorOyente(2, 0);

        verify(reporteJdbcRepository).findMinutosPorOyente(20, 40);
        assertEquals(100L, page.getTotalElements());
    }

    @Test
    void minutosPorGeneroPaginaConLosMismosLimites() {
        when(reporteJdbcRepository.findMinutosPorGenero(10, 0)).thenReturn(List.of());
        when(reporteJdbcRepository.countGeneros()).thenReturn(5L);

        reporteService.minutosPorGenero(0, 10);

        verify(reporteJdbcRepository).findMinutosPorGenero(10, 0);
    }

    @Test
    void reproduccionesPorMesAplicaDefecto24YMaximo120() {
        when(reporteJdbcRepository.findReproduccionesPorMes(24)).thenReturn(List.of());
        reporteService.reproduccionesPorMes(0);
        verify(reporteJdbcRepository).findReproduccionesPorMes(24);

        when(reporteJdbcRepository.findReproduccionesPorMes(120)).thenReturn(List.of());
        reporteService.reproduccionesPorMes(999);
        verify(reporteJdbcRepository).findReproduccionesPorMes(120);
    }

    @Test
    void usuariosPorAnioUsaElLimiteSolicitado() {
        when(reporteJdbcRepository.findUsuariosPorAnio(30)).thenReturn(List.of());
        reporteService.usuariosPorAnio(30);
        verify(reporteJdbcRepository).findUsuariosPorAnio(30);
    }
}
