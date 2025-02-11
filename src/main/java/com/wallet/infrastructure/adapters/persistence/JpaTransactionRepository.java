package com.wallet.infrastructure.adapters.persistence;

import com.wallet.infrastructure.adapters.persistence.entity.TransactionJpaEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

@Repository
public interface JpaTransactionRepository extends JpaRepository<TransactionJpaEntity, UUID> {
    List<TransactionJpaEntity> findByWalletId(UUID walletId);
}