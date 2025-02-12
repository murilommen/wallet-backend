package com.wallet.infrastructure.adapters.persistence.jpa;

import com.wallet.infrastructure.adapters.persistence.jpa.entity.WalletJpaEntity;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaWalletRepository extends CrudRepository<WalletJpaEntity, UUID> {
    Optional<WalletJpaEntity> findById(@NonNull UUID uuid);
}