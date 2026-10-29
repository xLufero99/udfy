package co.edu.udistrital.udfy.service;

import co.edu.udistrital.udfy.repository.TopCancionJdbcRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TopCancionServiceUnitTest {
    @Mock
    private TopCancionJdbcRepository topCancionJdbcRepository;
    @InjectMocks
    private TopCancionService topCancionService;

    @Test
    void usaLimitePorDefectoCuandoElLimiteEsInvalido() {
        when(topCancionJdbcRepository.findTopCanciones(4L, 10)).thenReturn(List.of());
        topCancionService.topCanciones(4L, 0);
        verify(topCancionJdbcRepository).findTopCanciones(4L, 10);
    }

    @Test
    void limitaElLimiteAMaximo50() {
        when(topCancionJdbcRepository.findTopCanciones(4L, 50)).thenReturn(List.of());
        topCancionService.topCanciones(4L, 999);
        verify(topCancionJdbcRepository).findTopCanciones(4L, 50);
    }

    @Test
    void usaElLimiteSolicitadoCuandoEsValido() {
        when(topCancionJdbcRepository.findTopCanciones(4L, 15)).thenReturn(List.of());
        topCancionService.topCanciones(4L, 15);
        verify(topCancionJdbcRepository).findTopCanciones(4L, 15);
    }
}
