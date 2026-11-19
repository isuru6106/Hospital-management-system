
package com.example.hospital_manager.config;

import com.example.hospital_manager.model.AppUser;
import com.example.hospital_manager.model.Role;
import com.example.hospital_manager.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataLoader {

    @Value("${application.admin.username:admin}")
    private String adminUsername;

    @Value("${application.admin.password:Admin@123}")
    private String adminPassword;

    @Bean
    public CommandLineRunner initializeData(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            if (userRepository.findByUsername(adminUsername).isEmpty()) {

                AppUser admin = new AppUser(
                        adminUsername,
                        passwordEncoder.encode(adminPassword),
                        Role.ADMIN
                );

                userRepository.save(admin);

                System.out.println(
                        "Default administrator account created successfully!"
                );

            } else {

                System.out.println(
                        "Administrator account already exists."
                );
            }
        };
    }
}
