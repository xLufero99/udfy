package co.edu.udistrital.udfy.service;

import co.edu.udistrital.udfy.dto.request.ActualizarUsuarioRequest;
import co.edu.udistrital.udfy.dto.response.UsuarioResponse;
import co.edu.udistrital.udfy.model.entity.Usuario;
import co.edu.udistrital.udfy.repository.UsuarioRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceUnitTest {
    @Mock
    private UsuarioRepository usuarioRepository;
    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void editarPerfilActualizaNombrePaisYEdad() {
        Usuario usuario = new Usuario();
        when(usuarioRepository.findById(7L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
        ActualizarUsuarioRequest request = new ActualizarUsuarioRequest("Ana", "Colombia", 20);

        UsuarioResponse response = usuarioService.editarPerfil(7L, request);

        assertEquals("Ana", usuario.getNombre());
        assertEquals("Colombia", usuario.getPais());
        assertEquals(20, usuario.getEdad());
        assertEquals("Ana", response.nombre());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void editarPerfilUsuarioInexistenteLanza400() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());
        ActualizarUsuarioRequest request = new ActualizarUsuarioRequest("Ana", "Colombia", 20);
        assertThrows(IllegalArgumentException.class, () -> usuarioService.editarPerfil(99L, request));
    }

    @Test
    void obtenerPerfilRetornaDatosDelUsuario() {
        Usuario usuario = new Usuario();
        org.springframework.test.util.ReflectionTestUtils.setField(usuario, "idUsuario", 7L);
        when(usuarioRepository.findById(7L)).thenReturn(Optional.of(usuario));
        UsuarioResponse response = usuarioService.obtenerPerfil(7L);
        assertEquals(7L, response.idUsuario());
    }

    @Test
    void obtenerPerfilUsuarioInexistenteLanza400() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> usuarioService.obtenerPerfil(99L));
    }
}
