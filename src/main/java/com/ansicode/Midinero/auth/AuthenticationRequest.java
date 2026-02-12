package com.ansicode.Midinero.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticationRequest {


    @NotEmpty(message = "El email es obligatorio")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotEmpty(message = "La contraseña es obligatoria")
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
