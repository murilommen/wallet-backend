package com.wallet.infrastructure.web.dto;

import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;


@Serdeable
@Data
@AllArgsConstructor
public class TransactionRequest {
    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;
}