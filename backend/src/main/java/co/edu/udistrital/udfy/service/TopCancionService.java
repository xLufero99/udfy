package co.edu.udistrital.udfy.service;

import co.edu.udistrital.udfy.dto.response.TopCancionResponse;
import co.edu.udistrital.udfy.repository.TopCancionJdbcRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TopCancionService {
    static final int LIMITE_DEFECTO = 10;
    static final int LIMITE_MAXIMO = 50;

    private final TopCancionJdbcRepository topCancionJdbcRepository;

    public TopCancionService(TopCancionJdbcRepository topCancionJdbcRepository) {
        this.topCancionJdbcRepository = topCancionJdbcRepository;
    }

    @Transactional(readOnly = true)
    public List<TopCancionResponse> topCanciones(Long idArtista, int limit) {
        int limite = limit < 1 ? LIMITE_DEFECTO : Math.min(limit, LIMITE_MAXIMO);
        return topCancionJdbcRepository.findTopCanciones(idArtista, limite);
    }
}
