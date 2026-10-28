package co.edu.udistrital.udfy.service;

import co.edu.udistrital.udfy.dto.request.ActualizarCancionRequest;
import co.edu.udistrital.udfy.dto.request.CancionRequest;
import co.edu.udistrital.udfy.dto.response.CancionResponse;
import co.edu.udistrital.udfy.model.entity.Artista;
import co.edu.udistrital.udfy.model.entity.Cancion;
import co.edu.udistrital.udfy.model.entity.Genero;
import co.edu.udistrital.udfy.repository.ArtistaRepository;
import co.edu.udistrital.udfy.repository.CancionRepository;
import co.edu.udistrital.udfy.repository.GeneroRepository;
import co.edu.udistrital.udfy.util.YoutubeIdExtractor;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CancionService {
    private final CancionRepository cancionRepository;
    private final ArtistaRepository artistaRepository;
    private final GeneroRepository generoRepository;
    private final YoutubeIdExtractor youtubeIdExtractor;

    public CancionService(CancionRepository cancionRepository, ArtistaRepository artistaRepository, 
                          GeneroRepository generoRepository, YoutubeIdExtractor youtubeIdExtractor) {
        this.cancionRepository = cancionRepository;
        this.artistaRepository = artistaRepository;
        this.generoRepository = generoRepository;
        this.youtubeIdExtractor = youtubeIdExtractor;
    }

    @Transactional
    public CancionResponse crear(Long idArtistaJwt, CancionRequest request) {
        String youtubeId = youtubeIdExtractor.extract(request.youtubeLink());
        Cancion cancion = new Cancion();
        cancion.setTitulo(request.titulo());
        cancion.setIdArtista(idArtistaJwt);
        cancion.setIdGenero(request.idGenero());
        cancion.setDuracionSegundos(request.duracionSegundos());
        cancion.setFechaLanzamiento(request.fechaLanzamiento());
        cancion.setYoutubeId(youtubeId);
        cancion = cancionRepository.save(cancion);
        return toResponse(cancion);
    }

    @Transactional
    public CancionResponse editar(Long idArtistaJwt, Long idCancion, ActualizarCancionRequest request) {
        Cancion cancion = cancionRepository.findByIdCancionAndIdArtista(idCancion, idArtistaJwt)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Canción no encontrada"));
        String youtubeId = youtubeIdExtractor.extract(request.youtubeLink());
        cancion.setTitulo(request.titulo());
        cancion.setIdGenero(request.idGenero());
        cancion.setDuracionSegundos(request.duracionSegundos());
        cancion.setFechaLanzamiento(request.fechaLanzamiento());
        cancion.setYoutubeId(youtubeId);
        cancion = cancionRepository.save(cancion);
        return toResponse(cancion);
    }

    public Page<CancionResponse> listarPorArtista(Long idArtista, int page, int size) {
        Pageable pageable = buildPageable(page, size);
        Page<Cancion> canciones = cancionRepository.findByIdArtista(idArtista, pageable);
        return canciones.map(this::toResponse);
    }

    public Page<CancionResponse> listarPorGenero(Long idGenero, int page, int size) {
        Pageable pageable = buildPageable(page, size);
        Page<Cancion> canciones = cancionRepository.findByIdGenero(idGenero, pageable);
        return canciones.map(this::toResponse);
    }

    private Pageable buildPageable(int page, int size) {
        int pageSize = size > 50 ? 50 : (size < 1 ? 20 : size);
        int pageNumber = page < 0 ? 0 : page;
        return PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "idCancion"));
    }

    private CancionResponse toResponse(Cancion cancion) {
        String nombreArtista = artistaRepository.findById(cancion.getIdArtista())
                .map(Artista::getNombre).orElse(null);
        String nombreGenero = generoRepository.findById(cancion.getIdGenero())
                .map(Genero::getNombre).orElse(null);
        return new CancionResponse(
                cancion.getIdCancion(),
                cancion.getTitulo(),
                cancion.getIdArtista(),
                nombreArtista,
                cancion.getIdGenero(),
                nombreGenero,
                cancion.getDuracionSegundos(),
                cancion.getFechaLanzamiento(),
                cancion.getYoutubeId()
        );
    }
}
