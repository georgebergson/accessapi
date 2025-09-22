package com.access.api.acessapi.service;

import com.access.api.acessapi.dto.RoleRequest;
import com.access.api.acessapi.entity.Role;
import com.access.api.acessapi.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Role createRole(RoleRequest request) {
        if (roleRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalStateException("Erro: Perfil com este nome já existe!");
        }

        Role role = Role.builder()
                .name(request.getName())
                .isAdmin(request.isAdmin())
                .build();

        return roleRepository.save(role);
    }
}
