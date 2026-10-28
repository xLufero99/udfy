package co.edu.udistrital.udfy.controller;

import co.edu.udistrital.udfy.dto.request.GeneroRequest;
import co.edu.udistrital.udfy.dto.response.GeneroResponse;
import co.edu.udistrital.udfy.service.GeneroService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/generos")
public class GeneroController {
    private final GeneroService generoService;

    public GeneroController(GeneroService generoService) {
        this.generoService = generoService;
    }

    @GetMapping
    public ResponseEntity<List<GeneroResponse>> listar() {
        return ResponseEntity.ok(generoService.listar());
    }

    @PostMapping
    public ResponseEntity<GeneroResponse> crear(@Valid @RequestBody GeneroRequest request) {
        GeneroResponse response = generoService.crear(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GeneroResponse> editar(@org.springframework.web.bind.annotation.PathVariable Long id, @Valid @RequestBody GeneroRequest request) {
        return ResponseEntity.ok(generoService.editar(id, request));
    }
}
