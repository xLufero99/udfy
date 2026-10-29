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
class RecomendacionJdbcRepositoryUnitTest {
    @Mock
    private JdbcTemplate jdbcTemplate;
    @InjectMocks
    private RecomendacionJdbcRepository repository;

    @Test
    void consultaUsaParametrosNuncaConcatenacion() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), any(Object[].class))).thenReturn(List.of());

        repository.findRecomendaciones(7L, 10);

        verify(jdbcTemplate).query(eq(RecomendacionJdbcRepository.SQL_RECOMENDACIONES), any(RowMapper.class),
                eq(7L), eq(7L), eq(10));
    }

    @Test
    void laConsultaTieneTresParametros() {
        long parametros = RecomendacionJdbcRepository.SQL_RECOMENDACIONES.chars()
                .filter(c -> c == '?').count();
        assertEquals(3, parametros);
    }
}
