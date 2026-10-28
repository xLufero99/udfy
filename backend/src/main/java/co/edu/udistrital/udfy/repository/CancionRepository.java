package co.edu.udistrital.udfy.repository;

import co.edu.udistrital.udfy.model.entity.Cancion;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CancionRepository extends JpaRepository<Cancion, Long> {
    Page<Cancion> findByIdArtista(Long idArtista, Pageable pageable);
    Page<Cancion> findByIdGenero(Long idGenero, Pageable pageable);
    Optional<Cancion> findByIdCancionAndIdArtista(Long idCancion, Long idArtista);
    boolean existsByIdCancionAndIdArtista(Long idCancion, Long idArtista);
}
