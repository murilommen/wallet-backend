package com.wallet.infrastructure.adapters.persistence;

import com.wallet.domain.model.Wallet;
import com.wallet.domain.port.outgoing.WalletRepository;
import com.wallet.infrastructure.adapters.persistence.entity.WalletJpaEntity;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.Optional;
import java.util.UUID;

@Singleton
public class JpaWalletRepositoryAdapter implements WalletRepository {

    @Inject
    JpaWalletRepository jpaWalletRepository;

    @Override
    public Optional<Wallet> findById(UUID id) {
        return jpaWalletRepository.findById(id)
                .map(JpaWalletRepositoryAdapter::mapToDomain);
    }

    @Override
    public Wallet save(Wallet wallet) {
        WalletJpaEntity jpaEntity = mapToJpaEntity(wallet);
        WalletJpaEntity savedJpaEntity = jpaWalletRepository.save(jpaEntity);
        return mapToDomain(savedJpaEntity);
    }

    @Override
    public Wallet update(Wallet wallet) {
        WalletJpaEntity jpaEntity = mapToJpaEntity(wallet);
        WalletJpaEntity updatedJpaEntity = jpaWalletRepository.update(jpaEntity);
        return mapToDomain(updatedJpaEntity);
    }

    @Override
    public Optional<Wallet> findByUserId(UUID userId) {
        return jpaWalletRepository.findByUserId(userId)
                .map(JpaWalletRepositoryAdapter::mapToDomain);
    }


    private static Wallet mapToDomain(WalletJpaEntity jpaEntity) {
        return new Wallet(
                jpaEntity.getId(),
                jpaEntity.getUserId(),
                jpaEntity.getBalance(),
                jpaEntity.getCreatedAt()
        );
    }

    private static WalletJpaEntity mapToJpaEntity(Wallet wallet) {
        WalletJpaEntity jpaEntity = new WalletJpaEntity();
        jpaEntity.setId(wallet.getId());
        jpaEntity.setUserId(wallet.getUserId());
        jpaEntity.setBalance(wallet.getBalance());
        jpaEntity.setCreatedAt(wallet.getCreatedAt());
        return jpaEntity;
    }
}
