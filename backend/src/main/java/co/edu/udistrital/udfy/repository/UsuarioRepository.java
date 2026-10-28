package co.edu.udistrital.udfy.repository;

import co.edu.udistrital.udfy.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
