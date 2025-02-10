package com.wallet.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class Wallet {

    private UUID id;
    private UUID userId;
    private BigDecimal balance;
    private OffsetDateTime createdAt;

    public Wallet(UUID userId) {
        this.userId = userId;
        this.balance = BigDecimal.ZERO;
        this.createdAt = OffsetDateTime.now();
    }

    public Wallet(UUID id, UUID userId, BigDecimal balance, OffsetDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.balance = balance;
        this.createdAt = createdAt;
    }

    public void deposit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }
}