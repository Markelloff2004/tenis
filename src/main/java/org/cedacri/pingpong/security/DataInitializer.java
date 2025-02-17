package org.cedacri.pingpong.security;

import org.cedacri.pingpong.entity.User;
import org.cedacri.pingpong.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ROLE_ADMIN");
            userRepository.save(admin);
        }

        if (userRepository.findByUsername("johndoe").isEmpty()) {
            User admin = new User();
            admin.setUsername("johndoe");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ROLE_ADMIN");
            userRepository.save(admin);
        }

        if (userRepository.findByUsername("user").isEmpty()) {
            User admin = new User();
            admin.setUsername("user");
            admin.setPassword(passwordEncoder.encode("user"));
            admin.setRole("ROLE_USER");
            userRepository.save(admin);
        }
    }
}
