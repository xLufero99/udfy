package co.edu.udistrital.udfy.seeder;

import co.edu.udistrital.udfy.model.entity.Cuenta;
import co.edu.udistrital.udfy.repository.CuentaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(AdminSeeder.class);
    private final CuentaRepository cuentaRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${admin.email:}")
    private String adminEmail;

    @Value("${admin.password:}")
    private String adminPassword;

    public AdminSeeder(CuentaRepository cuentaRepository) {
        this.cuentaRepository = cuentaRepository;
    }

    @Override
    public void run(String... args) {
        if (adminEmail == null || adminEmail.isBlank() || adminPassword == null || adminPassword.isBlank()) {
            logger.warn("ADMIN_EMAIL or ADMIN_PASSWORD not set; skipping admin seeder");
            return;
        }
        boolean existsAdmin = cuentaRepository.existsByRol("ADMIN");
        if (existsAdmin) {
            return;
        }
        String email = adminEmail.toLowerCase();
        Cuenta cuenta = new Cuenta();
        cuenta.setEmail(email);
        cuenta.setPasswordHash(passwordEncoder.encode(adminPassword));
        cuenta.setRol("ADMIN");
        cuenta.setIdUsuario(null);
        cuenta.setIdArtista(null);
        cuentaRepository.save(cuenta);
        logger.info("Admin account created for email: {}", email);
    }
}
