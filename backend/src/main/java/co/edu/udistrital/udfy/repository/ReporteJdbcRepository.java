package co.edu.udistrital.udfy.repository;

import co.edu.udistrital.udfy.dto.response.MinutosPorGeneroResponse;
import co.edu.udistrital.udfy.dto.response.MinutosPorOyenteResponse;
import co.edu.udistrital.udfy.dto.response.ReproduccionPorCancionResponse;
import co.edu.udistrital.udfy.dto.response.ReproduccionPorMesResponse;
import co.edu.udistrital.udfy.dto.response.UsuariosPorAnioResponse;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ReporteJdbcRepository {

    static final String SQL_REPRODUCCIONES_POR_CANCION = """
            SELECT c.id_cancion, c.titulo, a.nombre AS nombre_artista, g.nombre AS nombre_genero,
                   COUNT(r.id_reproduccion) AS total_reproducciones
            FROM cancion c
            JOIN artista a ON a.id_artista = c.id_artista
            JOIN genero g ON g.id_genero = c.id_genero
            LEFT JOIN reproduccion r ON r.id_cancion = c.id_cancion
            GROUP BY c.id_cancion, c.titulo, a.nombre, g.nombre
            ORDER BY total_reproducciones DESC, c.id_cancion DESC
            LIMIT ? OFFSET ?
            """;

    static final String SQL_COUNT_CANCIONES = "SELECT COUNT(*) FROM cancion";

    static final String SQL_MINUTOS_POR_OYENTE = """
            SELECT u.id_usuario, u.nombre, COALESCE(SUM(r.minutos_reproducidos), 0) AS total_minutos
            FROM usuario u
            LEFT JOIN reproduccion r ON r.id_usuario = u.id_usuario
            GROUP BY u.id_usuario, u.nombre
            ORDER BY total_minutos DESC, u.id_usuario DESC
            LIMIT ? OFFSET ?
            """;

    static final String SQL_COUNT_USUARIOS = "SELECT COUNT(*) FROM usuario";

    static final String SQL_MINUTOS_POR_GENERO = """
            SELECT g.id_genero, g.nombre, COALESCE(SUM(r.minutos_reproducidos), 0) AS total_minutos
            FROM genero g
            LEFT JOIN cancion c ON c.id_genero = g.id_genero
            LEFT JOIN reproduccion r ON r.id_cancion = c.id_cancion
            GROUP BY g.id_genero, g.nombre
            ORDER BY total_minutos DESC, g.id_genero DESC
            LIMIT ? OFFSET ?
            """;

    static final String SQL_COUNT_GENEROS = "SELECT COUNT(*) FROM genero";

    static final String SQL_REPRODUCCIONES_POR_MES = """
            SELECT TO_CHAR(fecha_reproduccion AT TIME ZONE 'America/Bogota', 'YYYY-MM') AS mes,
                   COUNT(*) AS total_reproducciones
            FROM reproduccion
            GROUP BY mes
            ORDER BY mes DESC
            LIMIT ?
            """;

    static final String SQL_USUARIOS_POR_ANIO = """
            SELECT EXTRACT(YEAR FROM fecha_registro AT TIME ZONE 'America/Bogota')::int AS anio,
                   COUNT(*) AS total_usuarios
            FROM usuario
            GROUP BY anio
            ORDER BY anio DESC
            LIMIT ?
            """;

    private static final RowMapper<ReproduccionPorCancionResponse> MAPPER_REPRODUCCIONES_POR_CANCION =
            (rs, rowNum) -> new ReproduccionPorCancionResponse(
                    rs.getLong("id_cancion"),
                    rs.getString("titulo"),
                    rs.getString("nombre_artista"),
                    rs.getString("nombre_genero"),
                    rs.getLong("total_reproducciones"));

    private static final RowMapper<MinutosPorOyenteResponse> MAPPER_MINUTOS_POR_OYENTE =
            (rs, rowNum) -> new MinutosPorOyenteResponse(
                    rs.getLong("id_usuario"),
                    rs.getString("nombre"),
                    rs.getBigDecimal("total_minutos"));

    private static final RowMapper<MinutosPorGeneroResponse> MAPPER_MINUTOS_POR_GENERO =
            (rs, rowNum) -> new MinutosPorGeneroResponse(
                    rs.getLong("id_genero"),
                    rs.getString("nombre"),
                    rs.getBigDecimal("total_minutos"));

    private static final RowMapper<ReproduccionPorMesResponse> MAPPER_REPRODUCCIONES_POR_MES =
            (rs, rowNum) -> new ReproduccionPorMesResponse(
                    rs.getString("mes"),
                    rs.getLong("total_reproducciones"));

    private static final RowMapper<UsuariosPorAnioResponse> MAPPER_USUARIOS_POR_ANIO =
            (rs, rowNum) -> new UsuariosPorAnioResponse(
                    rs.getInt("anio"),
                    rs.getLong("total_usuarios"));

    private final JdbcTemplate jdbcTemplate;

    public ReporteJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ReproduccionPorCancionResponse> findReproduccionesPorCancion(int limit, int offset) {
        return jdbcTemplate.query(SQL_REPRODUCCIONES_POR_CANCION, MAPPER_REPRODUCCIONES_POR_CANCION, limit, offset);
    }

    public long countCanciones() {
        Long total = jdbcTemplate.queryForObject(SQL_COUNT_CANCIONES, Long.class);
        return total == null ? 0 : total;
    }

    public List<MinutosPorOyenteResponse> findMinutosPorOyente(int limit, int offset) {
        return jdbcTemplate.query(SQL_MINUTOS_POR_OYENTE, MAPPER_MINUTOS_POR_OYENTE, limit, offset);
    }

    public long countUsuarios() {
        Long total = jdbcTemplate.queryForObject(SQL_COUNT_USUARIOS, Long.class);
        return total == null ? 0 : total;
    }

    public List<MinutosPorGeneroResponse> findMinutosPorGenero(int limit, int offset) {
        return jdbcTemplate.query(SQL_MINUTOS_POR_GENERO, MAPPER_MINUTOS_POR_GENERO, limit, offset);
    }

    public long countGeneros() {
        Long total = jdbcTemplate.queryForObject(SQL_COUNT_GENEROS, Long.class);
        return total == null ? 0 : total;
    }

    public List<ReproduccionPorMesResponse> findReproduccionesPorMes(int limit) {
        return jdbcTemplate.query(SQL_REPRODUCCIONES_POR_MES, MAPPER_REPRODUCCIONES_POR_MES, limit);
    }

    public List<UsuariosPorAnioResponse> findUsuariosPorAnio(int limit) {
        return jdbcTemplate.query(SQL_USUARIOS_POR_ANIO, MAPPER_USUARIOS_POR_ANIO, limit);
    }
}
