package co.edu.udistrital.udfy.repository;

import co.edu.udistrital.udfy.dto.response.RecomendacionResponse;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class RecomendacionJdbcRepository {

    static final String SQL_RECOMENDACIONES = """
            SELECT c.id_cancion, c.titulo, c.youtube_id, c.duracion_segundos,
                   a.nombre AS nombre_artista, g.nombre AS nombre_genero
            FROM cancion c
            JOIN artista a ON a.id_artista = c.id_artista
            JOIN genero g ON g.id_genero = c.id_genero
            LEFT JOIN (
                SELECT id_cancion, COUNT(*) AS total
                FROM reproduccion
                GROUP BY id_cancion
            ) p ON p.id_cancion = c.id_cancion
            WHERE NOT EXISTS (
                SELECT 1 FROM reproduccion r
                WHERE r.id_usuario = ? AND r.id_cancion = c.id_cancion
            )
            ORDER BY
                (c.id_genero = (
                    SELECT c2.id_genero
                    FROM reproduccion r2
                    JOIN cancion c2 ON c2.id_cancion = r2.id_cancion
                    WHERE r2.id_usuario = ?
                    GROUP BY c2.id_genero
                    ORDER BY SUM(r2.minutos_reproducidos) DESC, c2.id_genero
                    LIMIT 1
                )) DESC NULLS LAST,
                COALESCE(p.total, 0) DESC,
                c.id_cancion DESC
            LIMIT ?
            """;

    private static final RowMapper<RecomendacionResponse> ROW_MAPPER = (rs, rowNum) -> new RecomendacionResponse(
            rs.getLong("id_cancion"),
            rs.getString("titulo"),
            rs.getString("youtube_id"),
            rs.getInt("duracion_segundos"),
            rs.getString("nombre_artista"),
            rs.getString("nombre_genero"));

    private final JdbcTemplate jdbcTemplate;

    public RecomendacionJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<RecomendacionResponse> findRecomendaciones(Long idUsuario, int limit) {
        return jdbcTemplate.query(SQL_RECOMENDACIONES, ROW_MAPPER, idUsuario, idUsuario, limit);
    }
}
