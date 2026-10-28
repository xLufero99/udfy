package co.edu.udistrital.udfy.service;

import co.edu.udistrital.udfy.dto.request.ActualizarCancionRequest;
import co.edu.udistrital.udfy.repository.ArtistaRepository;
import co.edu.udistrital.udfy.repository.CancionRepository;
import co.edu.udistrital.udfy.repository.GeneroRepository;
import co.edu.udistrital.udfy.util.YoutubeIdExtractor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CancionServiceUnitTest {
    @Mock
    private CancionRepository cancionRepository;
    @Mock
    private ArtistaRepository artistaRepository;
    @Mock
    private GeneroRepository generoRepository;
    @Mock
    private YoutubeIdExtractor youtubeIdExtractor;
    @InjectMocks
    private CancionService cancionService;

    @Test
    void editarCancionAjenaLanza404YSinSave() {
        when(cancionRepository.findByIdCancionAndIdArtista(10L, 5L)).thenReturn(Optional.empty());
        ActualizarCancionRequest request = new ActualizarCancionRequest("Tema", 1L, 200, "https://www.youtube.com/watch?v=dQw4w9WgXcQ", LocalDate.of(2023, 1, 1));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> cancionService.editar(5L, 10L, request));
        assertEquals(404, ex.getStatusCode().value());
        verify(cancionRepository, never()).save(any());
    }
}
