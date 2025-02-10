package com.wallet.infrastructure.adapters.persistence;

import com.wallet.domain.model.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEntity {
    @Id
    private UUID id;

    @Column(name = "wallet_id", nullable = false)
    private UUID walletId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(name = "balance_after_transaction", nullable = false, precision = 19, scale = 4)
    private BigDecimal balanceAfterTransaction;

    @Column(nullable = false)
    private String reference;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}