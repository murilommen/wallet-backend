package com.wallet.infrastructure.web.dto;

import com.wallet.domain.model.Wallet;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Introspected
@Serdeable
@Data
public class WalletResponse {
    UUID id;
    UUID userId;
    BigDecimal balance;
    LocalDateTime createdAt;

    public WalletResponse(Wallet wallet) {
        this.id = wallet.getId();
        this.userId = wallet.getUserId();
        this.balance = wallet.getBalance();
        this.createdAt = wallet.getCreatedAt().toLocalDateTime();
    }
}