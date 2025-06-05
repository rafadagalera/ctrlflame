package br.com.ctrlflame.config;

import br.com.ctrlflame.model.ERole;
import br.com.ctrlflame.model.Role;
import br.com.ctrlflame.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.count() == 0) {
                Role roleUser = new Role(ERole.ROLE_USER);
                Role roleAdmin = new Role(ERole.ROLE_ADMIN);

                roleRepository.save(roleUser);
                roleRepository.save(roleAdmin);
            }
        };
    }
}