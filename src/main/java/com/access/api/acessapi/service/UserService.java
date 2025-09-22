package com.access.api.acessapi.service;

import com.access.api.acessapi.dto.AdminUserUpdateRequest;
import com.access.api.acessapi.dto.RegisterRequest;
import com.access.api.acessapi.dto.UserResponse;
import com.access.api.acessapi.dto.UserUpdateRequest;
import com.access.api.acessapi.entity.Role;
import com.access.api.acessapi.entity.User;
import com.access.api.acessapi.repository.RoleRepository;
import com.access.api.acessapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalStateException("Erro: Nome de usuário já está em uso!");
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(
                        () -> new IllegalStateException("ROLE_USER não encontrado. Por favor, inicialize os perfis."));

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(userRole)
                .build();

        return userRepository.save(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .role(user.getRole().getName())
                        .isAdmin(user.getRole().isAdmin())
                        .build())
                .collect(Collectors.toList());
    }

    public User updateUser(Long userId, UserUpdateRequest request) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new IllegalStateException("Usuário não encontrado.");
        }

        User user = optionalUser.get();

        // Atualiza o username se estiver presente no request
        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            user.setUsername(request.getUsername());
        }

        // Atualiza a senha se estiver presente no request
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Role não é alterada
        return userRepository.save(user);
    }

    public User adminUpdateUser(Long userId, AdminUserUpdateRequest request) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new IllegalStateException("Usuário não encontrado.");
        }

        User user = optionalUser.get();

        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            user.setUsername(request.getUsername());
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getRole() != null && !request.getRole().isEmpty()) {
            Role newRole = roleRepository.findByName(request.getRole())
                    .orElseThrow(() -> new IllegalStateException("Role não encontrada: " + request.getRole()));
            user.setRole(newRole);
        }

        return userRepository.save(user);
    }

    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }
}
