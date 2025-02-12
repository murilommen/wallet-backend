package com.wallet.infrastructure.adapters.persistence;


import com.wallet.domain.model.Wallet;
import com.wallet.domain.port.outgoing.WalletRepository;
import com.wallet.infrastructure.adapters.persistence.jpa.JpaWalletRepository;
import com.wallet.infrastructure.adapters.persistence.jpa.entity.WalletJpaEntity;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.util.Optional;
import java.util.UUID;

@Singleton
public class JpaWalletRepositoryAdapter implements WalletRepository {

    private final JpaWalletRepository jpaWalletRepository;

    @Inject
    public JpaWalletRepositoryAdapter(JpaWalletRepository jpaWalletRepository) {
        this.jpaWalletRepository = jpaWalletRepository;
    }

    @Override
    public Optional<Wallet> findById(UUID id) {
        return jpaWalletRepository.findById(id)
                .map(this::mapToDomain);
    }

    @Override
    @Transactional
    public Wallet save(Wallet wallet) {
        WalletJpaEntity jpaEntity = mapToJpaEntity(wallet);
        WalletJpaEntity savedJpaEntity = jpaWalletRepository.save(jpaEntity);
        return mapToDomain(savedJpaEntity);
    }

    @Override
    @Transactional
    public Wallet update(Wallet wallet) {
        if (!jpaWalletRepository.existsById(wallet.getId())) {
            throw new IllegalArgumentException("Wallet not found: " + wallet.getId());
        }
        WalletJpaEntity jpaEntity = mapToJpaEntity(wallet);
        WalletJpaEntity updatedJpaEntity = jpaWalletRepository.update(jpaEntity);
        return mapToDomain(updatedJpaEntity);
    }

    private Wallet mapToDomain(WalletJpaEntity jpaEntity) {
        return new Wallet(
                jpaEntity.getId(),
                jpaEntity.getUserId(),
                jpaEntity.getBalance(),
                jpaEntity.getCreatedAt()
        );
    }

    private WalletJpaEntity mapToJpaEntity(Wallet wallet) {
        return new WalletJpaEntity(
                wallet.getId(),
                wallet.getUserId(),
                wallet.getBalance(),
                wallet.getCreatedAt()
        );
    }
}
