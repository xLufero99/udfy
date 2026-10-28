package co.edu.udistrital.udfy.service;

import co.edu.udistrital.udfy.dto.request.GeneroRequest;
import co.edu.udistrital.udfy.dto.response.GeneroResponse;
import co.edu.udistrital.udfy.model.entity.Genero;
import co.edu.udistrital.udfy.repository.GeneroRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GeneroService {
    private final GeneroRepository generoRepository;

    public GeneroService(GeneroRepository generoRepository) {
        this.generoRepository = generoRepository;
    }

    public List<GeneroResponse> listar() {
        return generoRepository.findAll().stream()
                .map(g -> new GeneroResponse(g.getIdGenero(), g.getNombre()))
                .collect(Collectors.toList());
    }

    @Transactional
    public GeneroResponse crear(GeneroRequest request) {
        Genero genero = new Genero();
        genero.setNombre(request.nombre());
        genero = generoRepository.save(genero);
        return new GeneroResponse(genero.getIdGenero(), genero.getNombre());
    }

    @Transactional
    public GeneroResponse editar(Long id, GeneroRequest request) {
        Genero genero = generoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Género no encontrado"));
        genero.setNombre(request.nombre());
        genero = generoRepository.save(genero);
        return new GeneroResponse(genero.getIdGenero(), genero.getNombre());
    }
}
