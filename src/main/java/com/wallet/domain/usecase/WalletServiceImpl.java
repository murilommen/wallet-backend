package com.wallet.domain.usecase;

import com.wallet.domain.model.Transaction;
import com.wallet.domain.model.TransactionType;
import com.wallet.domain.model.Wallet;
import com.wallet.domain.port.incoming.WalletService;
import com.wallet.domain.port.outgoing.TransactionRepository;
import com.wallet.domain.port.outgoing.WalletRepository;
import com.wallet.model.WalletBalanceResponse;
import com.wallet.model.WalletCreateRequest;
import com.wallet.model.WalletResponse;
import com.wallet.model.TransactionRequest;
import com.wallet.model.TransferRequest;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Singleton // Still using Micronaut DI annotations here - often pragmatic in application layer
public class WalletServiceImpl implements WalletService {

    @Inject
    WalletRepository walletRepository; // Dependency on the OUTGOING port

    @Inject
    TransactionRepository transactionRepository; // Dependency on the OUTGOING port

    @Override
    @Transactional // Still using Micronaut Transactional - Infrastructure aspect, but acceptable here
    public WalletResponse createWallet(@Valid @NotNull WalletCreateRequest walletCreateRequest) {
        Wallet wallet = new Wallet(walletCreateRequest.getUserId());
        wallet = walletRepository.save(wallet); // Using the OUTGOING port

        // Create initial transaction for wallet creation (for audit)
        Transaction creationTransaction = new Transaction(
                wallet.getId(),
                TransactionType.CREATE_WALLET,
                BigDecimal.ZERO, // Initial balance is 0
                "Wallet Created",
                BigDecimal.ZERO,
                BigDecimal.ZERO // Balance before and after creation is 0
        );
        transactionRepository.save(creationTransaction); // Using the OUTGOING port

        return mapWalletToResponse(wallet);
    }

    // ... (rest of the methods - getWalletBalance, getHistoricalWalletBalance, depositFunds, withdrawFunds, transferFunds, getWalletById, getWalletByIdOrThrow, createAndSaveTransaction, mapWalletToResponse, mapWalletBalanceResponse -  similar logic as before, but using the domain Wallet entity and repository ports) ...

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
        List<Transaction> historicalTransactions = transactionRepository
                .findByWalletIdAndTransactionDateLessThanOrEqualToOrderByTransactionDateDesc(wallet.getId(), timestamp);

        BigDecimal historicalBalance = BigDecimal.ZERO;
        for (Transaction transaction : historicalTransactions) {
            if (transaction.getTransactionType() == TransactionType.CREATE_WALLET) {
                continue; // Skip creation transaction, balance starts at 0 before it.
            }
            historicalBalance = transaction.getPreviousBalance(); // Balance before the transaction is the balance at that point.
            break; // Transactions are ordered desc by date, so first one is closest to timestamp.
        }
        return historicalBalance;
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
        wallet.deposit(depositAmount); // Use domain logic on Wallet entity
        walletRepository.update(wallet); // Using OUTGOING port

        Transaction depositTransaction = createAndSaveTransaction(wallet, TransactionType.DEPOSIT, depositAmount, "Deposit", previousBalance, wallet.getBalance()); // Use wallet.getBalance()
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
        wallet.withdraw(withdrawalAmount); // Use domain logic on Wallet entity
        walletRepository.update(wallet); // Using OUTGOING port

        Transaction withdrawTransaction = createAndSaveTransaction(wallet, TransactionType.WITHDRAW, withdrawalAmount, "Withdrawal", previousBalance, wallet.getBalance()); // Use wallet.getBalance()
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
        fromWallet.withdraw(transferAmount); // Domain logic
        walletRepository.update(fromWallet); // OUTGOING port

        BigDecimal toWalletPreviousBalance = toWallet.getBalance();
        toWallet.deposit(transferAmount); // Domain logic
        walletRepository.update(toWallet); // OUTGOING port

        // Create TRANSFER_OUT transaction for sender
        Transaction transferOutTransaction = createAndSaveTransaction(fromWallet, TransactionType.TRANSFER_OUT, transferAmount, "Transfer to wallet " + toWallet.getId(), fromWalletPreviousBalance, fromWallet.getBalance()); // Use wallet.getBalance()

        // Create TRANSFER_IN transaction for receiver
        Transaction transferInTransaction = createAndSaveTransaction(toWallet, TransactionType.TRANSFER_IN, transferAmount, "Transfer from wallet " + fromWallet.getId(), toWalletPreviousBalance, toWallet.getBalance()); // Use wallet.getBalance()


        // Link related transactions (important for audit and tracing)
        transferOutTransaction.setRelatedTransactionId(transferInTransaction.getId());
        transactionRepository.save(transferOutTransaction); // OUTGOING port
        transferInTransaction.setRelatedTransactionId(transferOutTransaction.getId());
        transactionRepository.save(transferInTransaction); // OUTGOING port


        return mapWalletBalanceResponse(fromWallet); // Or maybe return balance of both? For now, sender's balance.
    }

    @Override
    public Wallet getWalletById(UUID walletId) {
        return getWalletByIdOrThrow(walletId);
    }


    private Wallet getWalletByIdOrThrow(UUID walletId) {
        return walletRepository.findById(walletId) // Using OUTGOING port
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found: " + walletId)); // Or custom exception
    }


    private Transaction createAndSaveTransaction(Wallet wallet, TransactionType transactionType, BigDecimal amount, String description, BigDecimal previousBalance, BigDecimal currentBalance) {
        Transaction transaction = new Transaction(wallet.getId(), transactionType, amount, description, previousBalance, currentBalance);
        return transactionRepository.save(transaction); // Using OUTGOING port
    }


    private WalletResponse mapWalletToResponse(Wallet wallet) {
        return new WalletResponse(wallet);
    }

    private WalletBalanceResponse mapWalletBalanceResponse(Wallet wallet) {
        return new WalletBalanceResponse(wallet.getBalance());
    }
}