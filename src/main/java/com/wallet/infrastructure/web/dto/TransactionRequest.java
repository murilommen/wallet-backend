package com.wallet.infrastructure.web.dto;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;


@Serdeable
@Data
public class TransactionRequest {
    @NotNull
    @DecimalMin(value = "0.01", inclusive = true) // Minimum amount for transactions
    private BigDecimal amount;
}