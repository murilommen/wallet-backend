package com.wallet.domain.usecase;

import com.wallet.domain.model.Transaction;
import com.wallet.domain.model.TransactionType;
import com.wallet.domain.model.Wallet;
import com.wallet.domain.port.outgoing.TransactionRepository;
import com.wallet.domain.port.outgoing.WalletRepository;
import com.wallet.infrastructure.web.dto.WalletBalanceResponse;
import com.wallet.infrastructure.web.dto.WalletCreateRequest;
import com.wallet.infrastructure.web.dto.TransactionRequest;
import com.wallet.infrastructure.web.dto.TransferRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WalletServiceImplTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private WalletServiceImpl walletService;

    private UUID testWalletId;
    private UUID testUserId;
    private Wallet testWallet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testWalletId = UUID.randomUUID();
        testUserId = UUID.randomUUID();
        testWallet = new Wallet(testUserId);
        testWallet.setId(testWalletId);
    }

    @Test
    void createWallet_Success() {
        WalletCreateRequest request = new WalletCreateRequest(testUserId);
        when(walletRepository.save(any(Wallet.class))).thenReturn(testWallet);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        var response = walletService.createWallet(request);

        assertNotNull(response);
        assertEquals(testWallet.getId(), response.getId());
        verify(walletRepository).save(any(Wallet.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void createWallet_NullRequest_ThrowsException() {
        assertThrows(NullPointerException.class, () -> walletService.createWallet(null));
    }

    @Test
    void depositFunds_Success() {
        BigDecimal depositAmount = new BigDecimal("100.00");
        TransactionRequest request = new TransactionRequest(depositAmount);
        when(walletRepository.findById(testWalletId)).thenReturn(Optional.of(testWallet));
        when(walletRepository.update(any(Wallet.class))).thenReturn(testWallet);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        WalletBalanceResponse response = walletService.depositFunds(testWalletId, request);

        assertNotNull(response);
        assertEquals(depositAmount, response.getBalance());
        verify(walletRepository).update(any(Wallet.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void depositFunds_NegativeAmount_ThrowsException() {
        TransactionRequest request = new TransactionRequest(new BigDecimal("-100.00"));
        when(walletRepository.findById(testWalletId)).thenReturn(Optional.of(testWallet));

        assertThrows(IllegalArgumentException.class,
                () -> walletService.depositFunds(testWalletId, request));
    }

    @Test
    void withdrawFunds_Success() {
        testWallet.deposit(new BigDecimal("200.00"));
        BigDecimal withdrawAmount = new BigDecimal("100.00");
        TransactionRequest request = new TransactionRequest(withdrawAmount);
        when(walletRepository.findById(testWalletId)).thenReturn(Optional.of(testWallet));
        when(walletRepository.update(any(Wallet.class))).thenReturn(testWallet);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        WalletBalanceResponse response = walletService.withdrawFunds(testWalletId, request);

        assertNotNull(response);
        assertEquals(new BigDecimal("100.00"), response.getBalance());
        verify(walletRepository).update(any(Wallet.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void withdrawFunds_InsufficientFunds_ThrowsException() {
        TransactionRequest request = new TransactionRequest(new BigDecimal("100.00"));
        when(walletRepository.findById(testWalletId)).thenReturn(Optional.of(testWallet));

        assertThrows(IllegalArgumentException.class,
                () -> walletService.withdrawFunds(testWalletId, request));
    }

    @Test
    void transferFunds_Success() {
        Wallet fromWallet = new Wallet(UUID.randomUUID());
        fromWallet.setId(UUID.randomUUID());
        fromWallet.deposit(new BigDecimal("200.00"));

        Wallet toWallet = new Wallet(UUID.randomUUID());
        toWallet.setId(UUID.randomUUID());

        TransferRequest request = new TransferRequest(fromWallet.getId(), toWallet.getId(), new BigDecimal("100.00"));

        when(walletRepository.findById(fromWallet.getId())).thenReturn(Optional.of(fromWallet));
        when(walletRepository.findById(toWallet.getId())).thenReturn(Optional.of(toWallet));
        when(walletRepository.update(any(Wallet.class))).thenReturn(fromWallet);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        WalletBalanceResponse response = walletService.transferFunds(request);

        assertNotNull(response);
        assertEquals(new BigDecimal("100.00"), response.getBalance());
        verify(walletRepository, times(2)).update(any(Wallet.class));
        verify(transactionRepository, times(2)).save(any(Transaction.class));
        verify(transactionRepository, times(2)).update(any(Transaction.class));
    }

    @Test
    void transferFunds_SameWallet_ThrowsException() {
        TransferRequest request = new TransferRequest(testWalletId, testWalletId, new BigDecimal("100.00"));

        assertThrows(IllegalArgumentException.class,
                () -> walletService.transferFunds(request));
    }

    @Test
    void getHistoricalWalletBalance_Success() {
        OffsetDateTime timestamp = OffsetDateTime.now();
        Transaction transaction1 = new Transaction(
                testWalletId,
                TransactionType.DEPOSIT,
                new BigDecimal("100.00"),
                "Test deposit",
                BigDecimal.ZERO,
                new BigDecimal("100.00")
        );
        transaction1.setTransactionDate(timestamp.minusDays(1));

        when(walletRepository.findById(testWalletId)).thenReturn(Optional.of(testWallet));
        when(transactionRepository.findByWalletIdAndTransactionDateLessThanOrEqualToOrderByTransactionDateDesc(
                eq(testWalletId), any(OffsetDateTime.class)
        )).thenReturn(new ArrayList<>(List.of(transaction1)));

        WalletBalanceResponse response = walletService.getHistoricalWalletBalance(testWalletId, timestamp);

        assertNotNull(response);
        assertEquals(new BigDecimal("100.00"), response.getBalance());
    }

    @Test
    void getHistoricalWalletBalance_NoTransactions_ReturnsZero() {
        OffsetDateTime timestamp = OffsetDateTime.now();
        when(walletRepository.findById(testWalletId)).thenReturn(Optional.of(testWallet));
        when(transactionRepository.findByWalletIdAndTransactionDateLessThanOrEqualToOrderByTransactionDateDesc(
                eq(testWalletId), any(OffsetDateTime.class)
        )).thenReturn(List.of());

        WalletBalanceResponse response = walletService.getHistoricalWalletBalance(testWalletId, timestamp);

        assertNotNull(response);
        assertEquals(BigDecimal.ZERO, response.getBalance());
    }

    @Test
    void getWalletBalance_WalletNotFound_ThrowsException() {
        when(walletRepository.findById(testWalletId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> walletService.getWalletBalance(testWalletId));
    }
}