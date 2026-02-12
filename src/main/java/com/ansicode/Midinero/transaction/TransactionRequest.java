package com.ansicode.Midinero.transaction;

import com.ansicode.Midinero.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {

    @NotBlank(message = "La descripcion es obligatoria")
    private String description;

    @NotNull(message = "El total es obligatorio ")
    @DecimalMin(value = "0.01", message = "El total debe ser mayor a 0")
    private BigDecimal total;

    @NotNull(message = "El tipo de transaccion es requerido")
    private TransactionType transactionType;

    @NotNull(message = "La categoria es requerida")
    private Long categoryId;
}
