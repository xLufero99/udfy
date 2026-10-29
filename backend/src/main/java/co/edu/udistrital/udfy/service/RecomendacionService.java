package co.edu.udistrital.udfy.service;

import co.edu.udistrital.udfy.dto.response.RecomendacionResponse;
import co.edu.udistrital.udfy.repository.RecomendacionJdbcRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecomendacionService {
    static final int LIMITE_DEFECTO = 10;
    static final int LIMITE_MAXIMO = 50;

    private final RecomendacionJdbcRepository recomendacionJdbcRepository;

    public RecomendacionService(RecomendacionJdbcRepository recomendacionJdbcRepository) {
        this.recomendacionJdbcRepository = recomendacionJdbcRepository;
    }

    @Transactional(readOnly = true)
    public List<RecomendacionResponse> recomendaciones(Long idUsuario, int limit) {
        int limite = limit < 1 ? LIMITE_DEFECTO : Math.min(limit, LIMITE_MAXIMO);
        return recomendacionJdbcRepository.findRecomendaciones(idUsuario, limite);
    }
}
