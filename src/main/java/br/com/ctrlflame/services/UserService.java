package br.com.ctrlflame.services;

import br.com.ctrlflame.exceptions.ResourceNotFoundException;
import br.com.ctrlflame.model.ERole;
import br.com.ctrlflame.model.Role;
import br.com.ctrlflame.model.User;
import br.com.ctrlflame.repository.RoleRepository;
import br.com.ctrlflame.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email já está em uso");
        }

        // Criptografa a senha
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Define as roles padrão (ROLE_USER)
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Role não encontrada"));
        roles.add(userRole);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public void updatePhone(String email, String phone) {
        User user = findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (phone != null && !phone.matches("^\\+[1-9]\\d{1,14}$")) {
            throw new IllegalArgumentException("Número de telefone inválido. Use o formato internacional: +5511999999999");
        }
        
        user.setPhone(phone);
        userRepository.save(user);
    }
} 