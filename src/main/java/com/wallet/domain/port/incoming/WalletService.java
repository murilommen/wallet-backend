package com.wallet.domain.port.incoming;

import com.wallet.domain.model.Wallet;
import com.wallet.model.WalletBalanceResponse;
import com.wallet.model.WalletCreateRequest;
import com.wallet.model.WalletResponse;

import com.wallet.model.TransactionRequest;
import com.wallet.model.TransferRequest;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface WalletService {
    WalletResponse createWallet(WalletCreateRequest walletCreateRequest);
    WalletBalanceResponse getWalletBalance(UUID walletId);
    WalletBalanceResponse getHistoricalWalletBalance(UUID walletId, OffsetDateTime timestamp);
    WalletBalanceResponse depositFunds(UUID walletId, TransactionRequest transactionRequest);
    WalletBalanceResponse withdrawFunds(UUID walletId, TransactionRequest transactionRequest);
    WalletBalanceResponse transferFunds(TransferRequest transferRequest);
    Wallet getWalletById(UUID walletId); // Internal use - retrieve Wallet entity
}