package com.wallet.infrastructure.adapters.persistence;

import com.wallet.domain.model.Transaction;
import com.wallet.domain.model.TransactionType;
import com.wallet.domain.port.outgoing.TransactionRepository;
import com.wallet.infrastructure.adapters.persistence.jpa.JpaTransactionRepository;
import com.wallet.infrastructure.adapters.persistence.jpa.entity.TransactionJpaEntity;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Singleton
public class JpaTransactionRepositoryAdapter implements TransactionRepository {

    private final JpaTransactionRepository jpaTransactionRepository;

    @Inject
    public JpaTransactionRepositoryAdapter(JpaTransactionRepository jpaTransactionRepository) {
        this.jpaTransactionRepository = jpaTransactionRepository;
    }

    @Override
    @Transactional
    public Transaction save(Transaction transaction) {
        TransactionJpaEntity jpaEntity = mapToJpaEntity(transaction);
        TransactionJpaEntity savedJpaEntity = jpaTransactionRepository.save(jpaEntity);
        return mapToDomain(savedJpaEntity);
    }

    @Override
    @Transactional
    public Transaction update(Transaction transaction) {
        TransactionJpaEntity jpaEntity = mapToJpaEntity(transaction);
        TransactionJpaEntity updatedJpaEntity = jpaTransactionRepository.update(jpaEntity);
        return mapToDomain(updatedJpaEntity);
    }

    @Override
    public List<Transaction> findByWalletIdAndTransactionDateLessThanOrEqualToOrderByTransactionDateDesc(UUID walletId, OffsetDateTime transactionDate) {
        List<TransactionJpaEntity> jpaEntities = jpaTransactionRepository.findByWalletId(walletId);
        return jpaEntities.stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    private Transaction mapToDomain(TransactionJpaEntity jpaEntity) {
        if (jpaEntity == null) return null;
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

    private TransactionJpaEntity mapToJpaEntity(Transaction transaction) {
        if (transaction == null) return null;
        return new TransactionJpaEntity(
                transaction.getId(),
                transaction.getWalletId(),
                transaction.getTransactionType().toString(),
                transaction.getAmount(),
                transaction.getTransactionDate(),
                transaction.getDescription(),
                transaction.getPreviousBalance(),
                transaction.getCurrentBalance(),
                transaction.getRelatedTransactionId()
        );
    }
}
