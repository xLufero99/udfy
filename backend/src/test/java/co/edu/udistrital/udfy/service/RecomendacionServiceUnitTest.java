package co.edu.udistrital.udfy.service;

import co.edu.udistrital.udfy.repository.RecomendacionJdbcRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecomendacionServiceUnitTest {
    @Mock
    private RecomendacionJdbcRepository recomendacionJdbcRepository;
    @InjectMocks
    private RecomendacionService recomendacionService;

    @Test
    void usaLimitePorDefectoCuandoElLimiteEsInvalido() {
        when(recomendacionJdbcRepository.findRecomendaciones(7L, 10)).thenReturn(List.of());
        assertTrue(recomendacionService.recomendaciones(7L, 0).isEmpty());
        verify(recomendacionJdbcRepository).findRecomendaciones(7L, 10);
    }

    @Test
    void limitaElLimiteAMaximo50() {
        when(recomendacionJdbcRepository.findRecomendaciones(7L, 50)).thenReturn(List.of());
        recomendacionService.recomendaciones(7L, 999);
        verify(recomendacionJdbcRepository).findRecomendaciones(7L, 50);
    }

    @Test
    void usaElLimiteSolicitadoCuandoEsValido() {
        when(recomendacionJdbcRepository.findRecomendaciones(7L, 5)).thenReturn(List.of());
        recomendacionService.recomendaciones(7L, 5);
        verify(recomendacionJdbcRepository).findRecomendaciones(7L, 5);
    }
}
