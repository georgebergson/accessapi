package com.access.api.acessapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "O campo 'username' não pode ser vazio")
    private String username;

    @NotBlank(message = "O campo 'password' não pode ser vazio")
    private String password;
}
