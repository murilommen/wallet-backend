package com.wallet.infrastructure.web.dto;

import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Serdeable
@Data
@AllArgsConstructor
public class TransferRequest {
    @NotNull
    private UUID fromWalletId;
    @NotNull
    private UUID toWalletId;
    @NotNull
    @DecimalMin(value = "0.01", inclusive = true)
    private BigDecimal amount;
}