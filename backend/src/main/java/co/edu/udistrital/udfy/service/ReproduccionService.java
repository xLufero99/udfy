package co.edu.udistrital.udfy.service;

import co.edu.udistrital.udfy.dto.request.RegistrarReproduccionRequest;
import co.edu.udistrital.udfy.dto.response.HistorialReproduccionResponse;
import co.edu.udistrital.udfy.dto.response.ReproduccionCreadaResponse;
import co.edu.udistrital.udfy.model.entity.Reproduccion;
import co.edu.udistrital.udfy.repository.ReproduccionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReproduccionService {
    private final ReproduccionRepository reproduccionRepository;

    public ReproduccionService(ReproduccionRepository reproduccionRepository) {
        this.reproduccionRepository = reproduccionRepository;
    }

    @Transactional
    public ReproduccionCreadaResponse registrar(Long idUsuarioJwt, RegistrarReproduccionRequest request) {
        Reproduccion reproduccion = new Reproduccion();
        reproduccion.setIdUsuario(idUsuarioJwt);
        reproduccion.setIdCancion(request.idCancion());
        reproduccion.setMinutosReproducidos(request.minutosReproducidos());
        reproduccion.setCompletada(request.completada());
        reproduccion = reproduccionRepository.save(reproduccion);
        return new ReproduccionCreadaResponse("Reproducción registrada", reproduccion.getIdReproduccion());
    }

    public Page<HistorialReproduccionResponse> historial(Long idUsuario, int page, int size) {
        Pageable pageable = buildPageable(page, size);
        return reproduccionRepository.findByUsuarioConDetalle(idUsuario, pageable);
    }

    private Pageable buildPageable(int page, int size) {
        int pageSize = size > 50 ? 50 : (size < 1 ? 20 : size);
        int pageNumber = page < 0 ? 0 : page;
        Sort sort = Sort.by(Sort.Direction.DESC, "fechaReproduccion")
                .and(Sort.by(Sort.Direction.DESC, "idReproduccion"));
        return PageRequest.of(pageNumber, pageSize, sort);
    }
}
