package com.wallet.model;

import com.wallet.domain.model.Wallet;
import lombok.Value;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
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