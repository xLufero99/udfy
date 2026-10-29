package co.edu.udistrital.udfy.controller;

import co.edu.udistrital.udfy.dto.response.MinutosPorGeneroResponse;
import co.edu.udistrital.udfy.dto.response.MinutosPorOyenteResponse;
import co.edu.udistrital.udfy.dto.response.ReproduccionPorCancionResponse;
import co.edu.udistrital.udfy.dto.response.ReproduccionPorMesResponse;
import co.edu.udistrital.udfy.dto.response.UsuariosPorAnioResponse;
import co.edu.udistrital.udfy.service.ReporteService;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {
    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/reproducciones-por-cancion")
    public ResponseEntity<Page<ReproduccionPorCancionResponse>> reproduccionesPorCancion(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(reporteService.reproduccionesPorCancion(page, size));
    }

    @GetMapping("/minutos-por-oyente")
    public ResponseEntity<Page<MinutosPorOyenteResponse>> minutosPorOyente(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(reporteService.minutosPorOyente(page, size));
    }

    @GetMapping("/minutos-por-genero")
    public ResponseEntity<Page<MinutosPorGeneroResponse>> minutosPorGenero(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(reporteService.minutosPorGenero(page, size));
    }

    @GetMapping("/reproducciones-por-mes")
    public ResponseEntity<List<ReproduccionPorMesResponse>> reproduccionesPorMes(
            @RequestParam(defaultValue = "24") int limit) {
        return ResponseEntity.ok(reporteService.reproduccionesPorMes(limit));
    }

    @GetMapping("/usuarios-por-anio")
    public ResponseEntity<List<UsuariosPorAnioResponse>> usuariosPorAnio(
            @RequestParam(defaultValue = "24") int limit) {
        return ResponseEntity.ok(reporteService.usuariosPorAnio(limit));
    }
}
