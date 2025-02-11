package com.wallet.infrastructure.adapters.persistence;

import com.wallet.domain.model.Transaction;
import com.wallet.domain.model.TransactionType;
import com.wallet.domain.port.outgoing.TransactionRepository;
import com.wallet.infrastructure.adapters.persistence.entity.TransactionJpaEntity;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Singleton
public class JpaTransactionRepositoryAdapter implements TransactionRepository {

    @Inject
    JpaTransactionRepository jpaTransactionRepository;

    @Override
    public Transaction save(Transaction transaction) {
        TransactionJpaEntity jpaEntity = mapToJpaEntity(transaction);
        TransactionJpaEntity savedJpaEntity = jpaTransactionRepository.save(jpaEntity);
        return mapToDomain(savedJpaEntity);
    }

    @Override
    public List<Transaction> findByWalletIdAndTransactionDateLessThanOrEqualToOrderByTransactionDateDesc(UUID walletId, OffsetDateTime transactionDate) {
        List<TransactionJpaEntity> jpaEntities = jpaTransactionRepository.findByWalletId(walletId);
        return jpaEntities.stream()
                .map(JpaTransactionRepositoryAdapter::mapToDomain)
                .collect(Collectors.toList());
    }

    private static Transaction mapToDomain(TransactionJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }
        return new Transaction(
                jpaEntity.getId(),
                jpaEntity.getWalletId(),
                TransactionType.valueOf(jpaEntity.getTransactionType()),
                jpaEntity.getAmount(),
                jpaEntity.getTransactionDate(),
                jpaEntity.getDescription(),
                jpaEntity.getPreviousBalance(),
                jpaEntity.getCurrentBalance(),
                jpaEntity.getRelatedTransactionId()
        );
    }

    private static TransactionJpaEntity mapToJpaEntity(Transaction transaction) {
        if (transaction == null) {
            return null;
        }
        TransactionJpaEntity jpaEntity = new TransactionJpaEntity();
        jpaEntity.setId(transaction.getId());
        jpaEntity.setWalletId(transaction.getWalletId());
        jpaEntity.setTransactionType(transaction.getTransactionType().toString()); // Convert Enum to String for JPA
        jpaEntity.setAmount(transaction.getAmount());
        jpaEntity.setTransactionDate(transaction.getTransactionDate());
        jpaEntity.setDescription(transaction.getDescription());
        jpaEntity.setPreviousBalance(transaction.getPreviousBalance());
        jpaEntity.setCurrentBalance(transaction.getCurrentBalance());
        jpaEntity.setRelatedTransactionId(transaction.getRelatedTransactionId());
        return jpaEntity;
    }
}