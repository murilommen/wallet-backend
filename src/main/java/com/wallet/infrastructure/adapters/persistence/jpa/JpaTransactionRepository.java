package com.wallet.infrastructure.adapters.persistence.jpa;

import com.wallet.infrastructure.adapters.persistence.jpa.entity.TransactionJpaEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaTransactionRepository extends CrudRepository<TransactionJpaEntity, UUID> {
    List<TransactionJpaEntity> findByWalletId(UUID walletId);
}
