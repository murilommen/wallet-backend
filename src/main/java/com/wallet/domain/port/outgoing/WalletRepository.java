package com.wallet.domain.port.outgoing;

import com.wallet.domain.model.Wallet;
import java.util.Optional;
import java.util.UUID;

public interface WalletRepository {
    Optional<Wallet> findById(UUID id);
    Wallet save(Wallet wallet);
    Wallet update(Wallet wallet);
}