package com.access.api.acessapi.controller;

import com.access.api.acessapi.dto.AdminUserUpdateRequest;
import com.access.api.acessapi.dto.LoginRequest;
import com.access.api.acessapi.dto.LoginResponse;
import com.access.api.acessapi.dto.RegisterRequest;
import com.access.api.acessapi.dto.RegisterResponse;
import com.access.api.acessapi.dto.UserResponse;
import com.access.api.acessapi.dto.UserUpdateRequest;
import com.access.api.acessapi.entity.User;
import com.access.api.acessapi.security.CustomUserDetails;
import com.access.api.acessapi.service.JwtService;
import com.access.api.acessapi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        final String token = jwtService.generateToken(userDetails);
        // Assuming UserDetails is our custom User entity
        Long id = ((User) userDetails).getId(); // Get ID from our custom User entity
        String userRole = userDetails.getAuthorities().stream().findFirst().map(GrantedAuthority::getAuthority)
                .orElse("ROLE_UNKNOWN");
        boolean isAdmin = userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return ResponseEntity.ok(new LoginResponse(id, token, userRole, isAdmin)); // Pass ID to constructor
    }

    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        userService.registerUser(request);
        return ResponseEntity.ok(new RegisterResponse("Usuário registrado com sucesso!"));
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> updateMyProfile(@Valid @RequestBody UserUpdateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User currentUser = (User) userDetailsService.loadUserByUsername(username);

        User updatedUser = userService.updateUser(currentUser.getId(), request);

        return ResponseEntity.ok(UserResponse.builder()
                .id(updatedUser.getId())
                .username(updatedUser.getUsername())
                .role(updatedUser.getRole().getName())
                .isAdmin(updatedUser.getRole().isAdmin())
                .build());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        CustomUserDetails authenticatedUser = (CustomUserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        boolean isAdmin = authenticatedUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !authenticatedUser.getId().equals(id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        User user = userService.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return ResponseEntity.ok(UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole().getName())
                .isAdmin(user.getRole().isAdmin())
                .build());
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
            @Valid @RequestBody AdminUserUpdateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails authenticatedUser = (CustomUserDetails) authentication.getPrincipal();

        boolean isAdmin = authenticatedUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // Usuário comum só pode atualizar o próprio ID
        if (!isAdmin && !authenticatedUser.getId().equals(id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        User updatedUser;
        if (isAdmin) {
            // Admin pode atualizar qualquer campo, inclusive role
            updatedUser = userService.adminUpdateUser(id, request);
        } else {
            // Usuário comum só pode atualizar campos permitidos (username/password)
            updatedUser = userService.updateUser(id, UserUpdateRequest.builder()
                    .username(request.getUsername())
                    .password(request.getPassword())
                    .build());
        }

        return ResponseEntity.ok(UserResponse.builder()
                .id(updatedUser.getId())
                .username(updatedUser.getUsername())
                .role(updatedUser.getRole().getName())
                .isAdmin(updatedUser.getRole().isAdmin())
                .build());
    }
}
