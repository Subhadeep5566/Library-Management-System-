package com.library.management;

import com.library.management.entity.User;
import com.library.management.entity.UserRole;
import com.library.management.entity.UserStatus;
import com.library.management.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableJpaAuditing
public class LibraryManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryManagementSystemApplication.class, args);
    }

    @Component
    @Profile("dev")
    static class DevDataInitializer implements CommandLineRunner {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;

        public DevDataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
            this.userRepository = userRepository;
            this.passwordEncoder = passwordEncoder;
        }

        @Override
        public void run(String... args) {
            if (!userRepository.existsByUsername("testuser")) {
                User user = User.builder()
                        .username("testuser")
                        .email("test@example.com")
                        .password(passwordEncoder.encode("password123"))
                        .firstName("Test")
                        .lastName("User")
                        .phone("1234567890")
                        .address("123 Test St")
                        .role(UserRole.MEMBER)
                        .status(UserStatus.ACTIVE)
                        .build();
                userRepository.save(user);
                System.out.println("Created test user: testuser / password123");
            }

            if (!userRepository.existsByUsername("admin")) {
                User admin = User.builder()
                        .username("admin")
                        .email("admin@example.com")
                        .password(passwordEncoder.encode("admin123"))
                        .firstName("Admin")
                        .lastName("User")
                        .role(UserRole.ADMIN)
                        .status(UserStatus.ACTIVE)
                        .build();
                userRepository.save(admin);
                System.out.println("Created admin user: admin / admin123");
            }
        }
    }
}