package com.wallet.domain.port.incoming;

import com.wallet.infrastructure.web.dto.WalletBalanceResponse;
import com.wallet.infrastructure.web.dto.WalletCreateRequest;
import com.wallet.infrastructure.web.dto.WalletResponse;

import com.wallet.infrastructure.web.dto.TransactionRequest;
import com.wallet.infrastructure.web.dto.TransferRequest;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface WalletService {
    WalletResponse createWallet(WalletCreateRequest walletCreateRequest);
    WalletBalanceResponse getWalletBalance(UUID walletId);
    WalletBalanceResponse getHistoricalWalletBalance(UUID walletId, OffsetDateTime timestamp);
    WalletBalanceResponse depositFunds(UUID walletId, TransactionRequest transactionRequest);
    WalletBalanceResponse withdrawFunds(UUID walletId, TransactionRequest transactionRequest);
    WalletBalanceResponse transferFunds(TransferRequest transferRequest);
}