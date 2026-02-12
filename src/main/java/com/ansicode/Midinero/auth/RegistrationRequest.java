package com.ansicode.Midinero.auth;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegistrationRequest {
    @NotEmpty(message = "El nombre es obligatorio")
    @NotBlank(message = "El nombre es obligatorio")
    private String firstname;

    @NotEmpty(message = "El apellido es obligatorio")
    @NotBlank(message = "El apellido es obligatorio")
    private String lastname;

    @Email(message = "El email no tiene un formato correcto")
    @NotEmpty(message = "El email es obligatorio")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotEmpty(message = "La contraseña es obligatoria")
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8 , message = "La contraseña debe tener al menos 8 caracteres")
    private String password;
}
