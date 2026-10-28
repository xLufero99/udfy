package co.edu.udistrital.udfy.service;

import co.edu.udistrital.udfy.dto.request.ActualizarArtistaRequest;
import co.edu.udistrital.udfy.dto.response.ArtistaResponse;
import co.edu.udistrital.udfy.model.entity.Artista;
import co.edu.udistrital.udfy.repository.ArtistaRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArtistaService {
    private final ArtistaRepository artistaRepository;

    public ArtistaService(ArtistaRepository artistaRepository) {
        this.artistaRepository = artistaRepository;
    }

    public List<ArtistaResponse> listar() {
        return artistaRepository.findAll().stream()
                .map(a -> new ArtistaResponse(a.getIdArtista(), a.getNombre(), a.getPais(), a.getEdad(), a.getIdGenero()))
                .collect(Collectors.toList());
    }

    public ArtistaResponse detalle(Long id) {
        Artista artista = artistaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Artista no encontrado"));
        return new ArtistaResponse(artista.getIdArtista(), artista.getNombre(), artista.getPais(), artista.getEdad(), artista.getIdGenero());
    }

    @Transactional
    public ArtistaResponse editarPerfil(Long idArtistaJwt, ActualizarArtistaRequest request) {
        Artista artista = artistaRepository.findById(idArtistaJwt)
                .orElseThrow(() -> new IllegalArgumentException("Artista no encontrado"));
        artista.setNombre(request.nombre());
        artista.setPais(request.pais());
        artista.setEdad(request.edad());
        artista.setIdGenero(request.idGenero());
        artista = artistaRepository.save(artista);
        return new ArtistaResponse(artista.getIdArtista(), artista.getNombre(), artista.getPais(), artista.getEdad(), artista.getIdGenero());
    }

    @Transactional
    public void eliminar(Long id) {
        if (!artistaRepository.existsById(id)) {
            throw new IllegalArgumentException("Artista no encontrado");
        }
        artistaRepository.deleteById(id);
        artistaRepository.flush();
    }
}
