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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TopCancionJdbcRepositoryUnitTest {
    @Mock
    private JdbcTemplate jdbcTemplate;
    @InjectMocks
    private TopCancionJdbcRepository repository;

    @Test
    void consultaUsaParametrosNuncaConcatenacion() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), any(Object[].class))).thenReturn(List.of());

        repository.findTopCanciones(4L, 10);

        verify(jdbcTemplate).query(eq(TopCancionJdbcRepository.SQL_TOP_ARTISTA), any(RowMapper.class),
                eq(4L), eq(10));
    }

    @Test
    void laConsultaTieneDosParametros() {
        long parametros = TopCancionJdbcRepository.SQL_TOP_ARTISTA.chars()
                .filter(c -> c == '?').count();
        assertEquals(2, parametros);
    }
}
