package com.ansicode.Midinero.handler;

import lombok.Getter;

import org.springframework.http.HttpStatus;

public enum BusinessErrorCodes {

    // General
    NO_CODE(0, HttpStatus.NOT_IMPLEMENTED, "Código de error no definido"),
    VALIDATION_ERROR(1002, HttpStatus.BAD_REQUEST, "Error de validación"),

    // AUTH / SECURITY (300-399)
    INCORRECT_PASSWORD(300, HttpStatus.BAD_REQUEST, "La contraseña es incorrecta"),
    NEW_PASSWORD_DOES_NOT_MATCH(301, HttpStatus.BAD_REQUEST, "Las contraseñas no coinciden"),
    ACCOUNT_LOCKED(302, HttpStatus.FORBIDDEN, "La cuenta de usuario está bloqueada"),
    ACCOUNT_DISABLED(303, HttpStatus.FORBIDDEN, "La cuenta de usuario está deshabilitada"),
    BAD_CREDENTIALS(304, HttpStatus.FORBIDDEN, "Usuario o contraseña incorrectos"),

    // USER (3000-3099)
    USER_NOT_FOUND(3000, HttpStatus.NOT_FOUND, "Usuario no encontrado"),
    EMAIL_ALREADY_EXISTS(3001, HttpStatus.CONFLICT, "El email ya está registrado"),

    // CATEGORY (200-299)
    CATEGORY_NOT_FOUND(200, HttpStatus.NOT_FOUND, "Categoría no encontrada"),
    CATEGORY_ALREADY_EXISTS(201, HttpStatus.CONFLICT, "La categoría ya existe"),

    // TRANSACTION (400-499)
    TRANSACTION_NOT_FOUND(400, HttpStatus.NOT_FOUND, "Transacción no encontrada"),
    TRANSACTION_DESCRIPTION_REQUIRED(401, HttpStatus.BAD_REQUEST, "La descripción de la transacción es obligatoria"),
    TRANSACTION_TOTAL_INVALID(402, HttpStatus.BAD_REQUEST, "El total debe ser mayor a 0"),
    TRANSACTION_TYPE_REQUIRED(403, HttpStatus.BAD_REQUEST, "El tipo de transacción es requerido"),
    TRANSACTION_CATEGORY_REQUIRED(404, HttpStatus.BAD_REQUEST, "La categoría es obligatoria"),

    // TOKEN / ACTIVATION (5000-5099)
    INVALID_TOKEN(5000, HttpStatus.BAD_REQUEST, "Token inválido"),
    TOKEN_EXPIRED(5001, HttpStatus.BAD_REQUEST, "El token ha expirado");

    @Getter
    private final int code;

    @Getter
    private final String description;

    @Getter
    private final HttpStatus httpStatus;

    BusinessErrorCodes(int code, HttpStatus httpStatus, String description) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.description = description;
    }
}
