package com.wallet.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class Transaction {

    private UUID id;
    private UUID walletId;
    private TransactionType transactionType;
    private BigDecimal amount;
    private OffsetDateTime transactionDate;
    private String description;
    private BigDecimal previousBalance;
    private BigDecimal currentBalance;
    private UUID relatedTransactionId;

    public Transaction(UUID walletId, TransactionType transactionType, BigDecimal amount, String description, BigDecimal previousBalance, BigDecimal currentBalance, UUID relatedTransactionId) {
        this.walletId = walletId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.transactionDate = OffsetDateTime.now();
        this.description = description;
        this.previousBalance = previousBalance;
        this.currentBalance = currentBalance;
        this.relatedTransactionId = relatedTransactionId;
    }

    public Transaction(UUID walletId, TransactionType transactionType, BigDecimal amount, String description, BigDecimal previousBalance, BigDecimal currentBalance) {
        this(walletId, transactionType, amount, description, previousBalance, currentBalance, null);
    }
}