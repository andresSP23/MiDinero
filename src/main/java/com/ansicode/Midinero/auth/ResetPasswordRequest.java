package com.ansicode.Midinero.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {

    @NotEmpty(message = "El token es obligatorio")
    @NotBlank(message = "El token es obligatorio")
    private String token;

    @NotEmpty(message = "La contraseña es obligatoria")
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String newPassword;

    @NotEmpty(message = "La confirmación de contraseña es obligatoria")
    @NotBlank(message = "La confirmación de contraseña es obligatoria")
    private String confirmPassword;
}
