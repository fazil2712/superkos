package com.superkos.app;

import com.superkos.app.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SuperkosApplication {
    public static void main(String[] args) {
        SpringApplication.run(SuperkosApplication.class, args);
    }

    @Bean
    public CommandLineRunner cleanupOrphanedUsers(UserRepository userRepository) {
        return args -> {
            try {
                userRepository.deleteOrphanedUsers();
                System.out.println(">>> Cleaned up orphaned users from database.");
            } catch (Exception e) {
                System.err.println(">>> Failed to clean up orphaned users: " + e.getMessage());
            }
        };
    }
}
