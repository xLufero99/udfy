package co.edu.udistrital.udfy.repository;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReporteJdbcRepositoryUnitTest {
    @Mock
    private JdbcTemplate jdbcTemplate;
    @InjectMocks
    private ReporteJdbcRepository repository;

    @Test
    void reproduccionesPorCancionUsaParametrosNuncaConcatenacion() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), any(Object[].class))).thenReturn(List.of());

        repository.findReproduccionesPorCancion(20, 40);

        verify(jdbcTemplate).query(eq(ReporteJdbcRepository.SQL_REPRODUCCIONES_POR_CANCION), any(RowMapper.class),
                eq(20), eq(40));
        assertTrue(ReporteJdbcRepository.SQL_REPRODUCCIONES_POR_CANCION.contains("?")
                && !ReporteJdbcRepository.SQL_REPRODUCCIONES_POR_CANCION.contains("'20'"));
    }

    @Test
    void minutosPorOyenteUsaParametros() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), any(Object[].class))).thenReturn(List.of());
        repository.findMinutosPorOyente(20, 0);
        verify(jdbcTemplate).query(eq(ReporteJdbcRepository.SQL_MINUTOS_POR_OYENTE), any(RowMapper.class),
                eq(20), eq(0));
    }

    @Test
    void minutosPorGeneroUsaParametros() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), any(Object[].class))).thenReturn(List.of());
        repository.findMinutosPorGenero(20, 0);
        verify(jdbcTemplate).query(eq(ReporteJdbcRepository.SQL_MINUTOS_POR_GENERO), any(RowMapper.class),
                eq(20), eq(0));
    }

    @Test
    void reproduccionesPorMesUsaZonaHorariaDeBogotaYParametro() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), any(Object[].class))).thenReturn(List.of());
        repository.findReproduccionesPorMes(24);
        verify(jdbcTemplate).query(eq(ReporteJdbcRepository.SQL_REPRODUCCIONES_POR_MES), any(RowMapper.class),
                eq(24));
        assertTrue(ReporteJdbcRepository.SQL_REPRODUCCIONES_POR_MES.contains("AT TIME ZONE 'America/Bogota'"));
    }

    @Test
    void usuariosPorAnioUsaZonaHorariaDeBogotaYParametro() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), any(Object[].class))).thenReturn(List.of());
        repository.findUsuariosPorAnio(24);
        verify(jdbcTemplate).query(eq(ReporteJdbcRepository.SQL_USUARIOS_POR_ANIO), any(RowMapper.class),
                eq(24));
        assertTrue(ReporteJdbcRepository.SQL_USUARIOS_POR_ANIO.contains("AT TIME ZONE 'America/Bogota'"));
    }

    @Test
    void conteosUsanQueryForObject() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class))).thenReturn(7L);
        assertEquals(7L, repository.countCanciones());
        assertEquals(7L, repository.countUsuarios());
        assertEquals(7L, repository.countGeneros());
    }
}
