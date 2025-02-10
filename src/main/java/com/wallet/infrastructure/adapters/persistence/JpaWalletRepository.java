package com.wallet.infrastructure.adapters.persistence;

import com.wallet.domain.model.Wallet;
import com.wallet.domain.port.outgoing.WalletRepository;
import com.wallet.infrastructure.adapters.persistence.entity.WalletJpaEntity; // JPA Entity
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.Optional;
import java.util.UUID;

@Repository // Micronaut Data JPA Repository - Infrastructure detail
public interface JpaWalletRepository extends JpaRepository<WalletJpaEntity, UUID> {
    Optional<WalletJpaEntity> findByUserId(UUID userId);
}

