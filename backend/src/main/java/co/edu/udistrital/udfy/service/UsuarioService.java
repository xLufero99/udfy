package co.edu.udistrital.udfy.service;

import co.edu.udistrital.udfy.dto.request.ActualizarUsuarioRequest;
import co.edu.udistrital.udfy.dto.response.UsuarioResponse;
import co.edu.udistrital.udfy.model.entity.Usuario;
import co.edu.udistrital.udfy.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioResponse obtenerPerfil(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return toResponse(usuario);
    }

    @Transactional
    public UsuarioResponse editarPerfil(Long idUsuario, ActualizarUsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        usuario.setNombre(request.nombre());
        usuario.setPais(request.pais());
        usuario.setEdad(request.edad());
        usuario = usuarioRepository.save(usuario);
        return toResponse(usuario);
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getPais(),
                usuario.getEdad(),
                usuario.getFechaRegistro()
        );
    }
}
