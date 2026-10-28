package co.edu.udistrital.udfy.security.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoderUtil {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }
}
