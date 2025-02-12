package com.wallet.domain.usecase;

import com.wallet.domain.model.Transaction;
import com.wallet.domain.model.TransactionType;
import com.wallet.domain.model.Wallet;
import com.wallet.domain.port.incoming.WalletService;
import com.wallet.domain.port.outgoing.TransactionRepository;
import com.wallet.domain.port.outgoing.WalletRepository;
import com.wallet.infrastructure.web.dto.WalletBalanceResponse;
import com.wallet.infrastructure.web.dto.WalletCreateRequest;
import com.wallet.infrastructure.web.dto.WalletResponse;
import com.wallet.infrastructure.web.dto.TransactionRequest;
import com.wallet.infrastructure.web.dto.TransferRequest;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Singleton
public class WalletServiceImpl implements WalletService {

    @Inject
    WalletRepository walletRepository;

    @Inject
    TransactionRepository transactionRepository;

    @Override
    @Transactional
    public WalletResponse createWallet(@Valid @NotNull WalletCreateRequest walletCreateRequest) {
        Wallet wallet = new Wallet(walletCreateRequest.getUserId());
        wallet = walletRepository.save(wallet);

        Transaction creationTransaction = new Transaction(
                wallet.getId(),
                TransactionType.CREATE_WALLET,
                BigDecimal.ZERO,
                "Wallet Created",
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
        transactionRepository.save(creationTransaction);

        return mapWalletToResponse(wallet);
    }

    @Override
    public WalletBalanceResponse getWalletBalance(UUID walletId) {
        Wallet wallet = getWalletByIdOrThrow(walletId);
        return mapWalletBalanceResponse(wallet);
    }

    @Override
    public WalletBalanceResponse getHistoricalWalletBalance(UUID walletId, OffsetDateTime timestamp) {
        Wallet wallet = getWalletByIdOrThrow(walletId);
        BigDecimal historicalBalance = calculateHistoricalBalance(wallet, timestamp);
        return new WalletBalanceResponse(historicalBalance);
    }

    private BigDecimal calculateHistoricalBalance(Wallet wallet, OffsetDateTime timestamp) {
        log.debug("Calculating historical balance for wallet ID: {}, timestamp: {}", wallet.getId(), timestamp);

        List<Transaction> historicalTransactions = transactionRepository
                .findByWalletIdAndTransactionDateLessThanOrEqualToOrderByTransactionDateDesc(wallet.getId(), timestamp);

        log.debug("Number of historical transactions found: {}", historicalTransactions.size());

        if (historicalTransactions.isEmpty()) {
            log.debug("No historical transactions found for this wallet and timestamp.");
            return BigDecimal.ZERO;
        }

        log.debug("Historical transactions found:");
        for (Transaction tx : historicalTransactions) {
            log.debug("  Transaction ID: {}, Type: {}, Date: {}, Current Balance: {}, Previous Balance: {}", tx.getId(), tx.getTransactionType(), tx.getTransactionDate(), tx.getCurrentBalance(), tx.getPreviousBalance());
        }
        historicalTransactions.sort((t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate()));

        for (Transaction transaction : historicalTransactions) {
            if (!transaction.getTransactionDate().isAfter(timestamp)) {
                BigDecimal historicalBalance = transaction.getCurrentBalance();
                log.debug("Found matching transaction at {} with balance: {}", transaction.getTransactionDate(), historicalBalance);
                return historicalBalance;
            }
        }

        log.debug("No transactions found before or at the specified timestamp.");
        return BigDecimal.ZERO;
    }


    @Override
    @Transactional
    public WalletBalanceResponse depositFunds(UUID walletId, @Valid @NotNull TransactionRequest transactionRequest) {
        Wallet wallet = getWalletByIdOrThrow(walletId);
        BigDecimal depositAmount = transactionRequest.getAmount();
        if (depositAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive."); // Or a custom exception
        }

        BigDecimal previousBalance = wallet.getBalance();
        wallet.deposit(depositAmount);
        walletRepository.update(wallet);

        createAndSaveTransaction(wallet, TransactionType.DEPOSIT, depositAmount, "Deposit", previousBalance, wallet.getBalance());
        return mapWalletBalanceResponse(wallet);
    }

    @Override
    @Transactional
    public WalletBalanceResponse withdrawFunds(UUID walletId, @Valid @NotNull TransactionRequest transactionRequest) {
        Wallet wallet = getWalletByIdOrThrow(walletId);
        BigDecimal withdrawalAmount = transactionRequest.getAmount();
        if (withdrawalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        if (wallet.getBalance().compareTo(withdrawalAmount) < 0) {
            throw new IllegalArgumentException("Insufficient funds.");
        }

        BigDecimal previousBalance = wallet.getBalance();
        wallet.withdraw(withdrawalAmount);
        walletRepository.update(wallet);

        createAndSaveTransaction(wallet, TransactionType.WITHDRAW, withdrawalAmount, "Withdrawal", previousBalance, wallet.getBalance()); // Use wallet.getBalance()
        return mapWalletBalanceResponse(wallet);
    }

    @Override
    @Transactional
    public WalletBalanceResponse transferFunds(@Valid @NotNull TransferRequest transferRequest) {
        if (transferRequest.getFromWalletId().equals(transferRequest.getToWalletId())) {
            throw new IllegalArgumentException("Cannot transfer funds to the same wallet.");
        }
        Wallet fromWallet = getWalletByIdOrThrow(transferRequest.getFromWalletId());
        Wallet toWallet = getWalletByIdOrThrow(transferRequest.getToWalletId());
        BigDecimal transferAmount = transferRequest.getAmount();

        if (transferAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive.");
        }
        if (fromWallet.getBalance().compareTo(transferAmount) < 0) {
            throw new IllegalArgumentException("Insufficient funds for transfer.");
        }

        BigDecimal fromWalletPreviousBalance = fromWallet.getBalance();
        fromWallet.withdraw(transferAmount);
        walletRepository.update(fromWallet);

        BigDecimal toWalletPreviousBalance = toWallet.getBalance();
        toWallet.deposit(transferAmount);
        walletRepository.update(toWallet);

        Transaction transferOutTransaction = createAndSaveTransaction(fromWallet, TransactionType.TRANSFER_OUT, transferAmount, "Transfer to wallet " + toWallet.getId(), fromWalletPreviousBalance, fromWallet.getBalance()); // Use wallet.getBalance()

        Transaction transferInTransaction = createAndSaveTransaction(toWallet, TransactionType.TRANSFER_IN, transferAmount, "Transfer from wallet " + fromWallet.getId(), toWalletPreviousBalance, toWallet.getBalance()); // Use wallet.getBalance()

        transferOutTransaction.setRelatedTransactionId(transferInTransaction.getId());
        transactionRepository.update(transferOutTransaction);
        transferInTransaction.setRelatedTransactionId(transferOutTransaction.getId());
        transactionRepository.update(transferInTransaction);


        return mapWalletBalanceResponse(fromWallet);
    }


    private Wallet getWalletByIdOrThrow(UUID walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found: " + walletId)); // Or custom exception
    }


    private Transaction createAndSaveTransaction(Wallet wallet, TransactionType transactionType, BigDecimal amount, String description, BigDecimal previousBalance, BigDecimal currentBalance) {
        Transaction transaction = new Transaction(wallet.getId(), transactionType, amount, description, previousBalance, currentBalance);
        return transactionRepository.save(transaction);
    }


    private WalletResponse mapWalletToResponse(Wallet wallet) {
        return new WalletResponse(wallet);
    }

    private WalletBalanceResponse mapWalletBalanceResponse(Wallet wallet) {
        return new WalletBalanceResponse(wallet.getBalance());
    }
}