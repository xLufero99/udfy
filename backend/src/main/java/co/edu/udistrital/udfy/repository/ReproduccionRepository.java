package co.edu.udistrital.udfy.repository;

import co.edu.udistrital.udfy.dto.response.HistorialReproduccionResponse;
import co.edu.udistrital.udfy.model.entity.Reproduccion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReproduccionRepository extends JpaRepository<Reproduccion, Long> {

    @Query("""
            SELECT new co.edu.udistrital.udfy.dto.response.HistorialReproduccionResponse(
                r.idReproduccion, r.minutosReproducidos, r.completada, r.fechaReproduccion,
                c.idCancion, c.titulo, a.nombre, g.nombre)
            FROM Reproduccion r
            JOIN Cancion c ON c.idCancion = r.idCancion
            JOIN Artista a ON a.idArtista = c.idArtista
            JOIN Genero g ON g.idGenero = c.idGenero
            WHERE r.idUsuario = :idUsuario
            """)
    Page<HistorialReproduccionResponse> findByUsuarioConDetalle(@Param("idUsuario") Long idUsuario, Pageable pageable);
}
