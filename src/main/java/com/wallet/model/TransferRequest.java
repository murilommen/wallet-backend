package com.wallet.model;

import lombok.Data;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TransferRequest {
    @NotNull
    private UUID fromWalletId;
    @NotNull
    private UUID toWalletId;
    @NotNull
    @DecimalMin(value = "0.01", inclusive = true)
    private BigDecimal amount;
}