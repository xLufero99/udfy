package co.edu.udistrital.udfy.repository;

import co.edu.udistrital.udfy.model.entity.Cuenta;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    Optional<Cuenta> findByEmail(String email);
    boolean existsByRol(String rol);
    boolean existsByEmail(String email);
}
