package co.edu.udistrital.udfy.service;

import co.edu.udistrital.udfy.dto.response.MinutosPorGeneroResponse;
import co.edu.udistrital.udfy.dto.response.MinutosPorOyenteResponse;
import co.edu.udistrital.udfy.dto.response.ReproduccionPorCancionResponse;
import co.edu.udistrital.udfy.dto.response.ReproduccionPorMesResponse;
import co.edu.udistrital.udfy.dto.response.UsuariosPorAnioResponse;
import co.edu.udistrital.udfy.repository.ReporteJdbcRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReporteService {
    static final int LIMITE_DEFECTO = 24;
    static final int LIMITE_MAXIMO = 120;

    private final ReporteJdbcRepository reporteJdbcRepository;

    public ReporteService(ReporteJdbcRepository reporteJdbcRepository) {
        this.reporteJdbcRepository = reporteJdbcRepository;
    }

    @Transactional(readOnly = true)
    public Page<ReproduccionPorCancionResponse> reproduccionesPorCancion(int page, int size) {
        PageParams params = normalizar(page, size);
        List<ReproduccionPorCancionResponse> content =
                reporteJdbcRepository.findReproduccionesPorCancion(params.pageSize(), params.offset());
        return new PageImpl<>(content, PageRequest.of(params.pageNumber(), params.pageSize()),
                reporteJdbcRepository.countCanciones());
    }

    @Transactional(readOnly = true)
    public Page<MinutosPorOyenteResponse> minutosPorOyente(int page, int size) {
        PageParams params = normalizar(page, size);
        List<MinutosPorOyenteResponse> content =
                reporteJdbcRepository.findMinutosPorOyente(params.pageSize(), params.offset());
        return new PageImpl<>(content, PageRequest.of(params.pageNumber(), params.pageSize()),
                reporteJdbcRepository.countUsuarios());
    }

    @Transactional(readOnly = true)
    public Page<MinutosPorGeneroResponse> minutosPorGenero(int page, int size) {
        PageParams params = normalizar(page, size);
        List<MinutosPorGeneroResponse> content =
                reporteJdbcRepository.findMinutosPorGenero(params.pageSize(), params.offset());
        return new PageImpl<>(content, PageRequest.of(params.pageNumber(), params.pageSize()),
                reporteJdbcRepository.countGeneros());
    }

    @Transactional(readOnly = true)
    public List<ReproduccionPorMesResponse> reproduccionesPorMes(int limit) {
        return reporteJdbcRepository.findReproduccionesPorMes(clampLimite(limit));
    }

    @Transactional(readOnly = true)
    public List<UsuariosPorAnioResponse> usuariosPorAnio(int limit) {
        return reporteJdbcRepository.findUsuariosPorAnio(clampLimite(limit));
    }

    private int clampLimite(int limit) {
        return limit < 1 ? LIMITE_DEFECTO : Math.min(limit, LIMITE_MAXIMO);
    }

    private PageParams normalizar(int page, int size) {
        int pageSize = size > 50 ? 50 : (size < 1 ? 20 : size);
        int pageNumber = page < 0 ? 0 : page;
        return new PageParams(pageNumber, pageSize, pageNumber * pageSize);
    }

    record PageParams(int pageNumber, int pageSize, int offset) {
    }
}
