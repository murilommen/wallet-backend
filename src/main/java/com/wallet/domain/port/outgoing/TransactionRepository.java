package com.wallet.domain.port.outgoing;

import com.wallet.domain.model.Transaction;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository {
    Transaction save(Transaction transaction);
    void update(Transaction transaction);
    List<Transaction> findByWalletIdAndTransactionDateLessThanOrEqualToOrderByTransactionDateDesc(UUID walletId, OffsetDateTime transactionDate);
}