package co.edu.udistrital.udfy.repository;

import co.edu.udistrital.udfy.dto.response.TopCancionResponse;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class TopCancionJdbcRepository {

    static final String SQL_TOP_ARTISTA = """
            SELECT c.id_cancion, c.titulo, c.youtube_id, c.duracion_segundos,
                   g.nombre AS nombre_genero,
                   COUNT(r.id_reproduccion) AS total_reproducciones
            FROM cancion c
            JOIN genero g ON g.id_genero = c.id_genero
            LEFT JOIN reproduccion r ON r.id_cancion = c.id_cancion
            WHERE c.id_artista = ?
            GROUP BY c.id_cancion, c.titulo, c.youtube_id, c.duracion_segundos, g.nombre
            ORDER BY total_reproducciones DESC, c.id_cancion DESC
            LIMIT ?
            """;

    private static final RowMapper<TopCancionResponse> ROW_MAPPER = (rs, rowNum) -> new TopCancionResponse(
            rs.getLong("id_cancion"),
            rs.getString("titulo"),
            rs.getString("youtube_id"),
            rs.getInt("duracion_segundos"),
            rs.getString("nombre_genero"),
            rs.getLong("total_reproducciones"));

    private final JdbcTemplate jdbcTemplate;

    public TopCancionJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<TopCancionResponse> findTopCanciones(Long idArtista, int limit) {
        return jdbcTemplate.query(SQL_TOP_ARTISTA, ROW_MAPPER, idArtista, limit);
    }
}
