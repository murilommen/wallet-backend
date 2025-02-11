package com.wallet.infrastructure.adapters.persistence;

import com.wallet.infrastructure.adapters.persistence.entity.WalletJpaEntity; // JPA Entity
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaWalletRepository extends JpaRepository<WalletJpaEntity, UUID> {
    Optional<WalletJpaEntity> findByUserId(UUID userId);
}

