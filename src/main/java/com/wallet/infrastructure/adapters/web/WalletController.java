package com.wallet.infrastructure.adapters.web;

import com.wallet.domain.port.incoming.WalletService;
import com.wallet.exceptions.WalletNotFoundException;
import com.wallet.infrastructure.web.dto.WalletBalanceResponse;
import com.wallet.infrastructure.web.dto.WalletCreateRequest;
import com.wallet.infrastructure.web.dto.WalletResponse;
import com.wallet.infrastructure.web.dto.TransactionRequest;
import com.wallet.infrastructure.web.dto.TransferRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.micronaut.http.annotation.Error;
import io.micronaut.retry.annotation.Retryable;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.UUID;

@Controller("/wallets")
public class WalletController {

    @Inject
    WalletService walletService;

    @Retryable
    @Post
    public HttpResponse<WalletResponse> createWallet(@Body @Valid @NotNull WalletCreateRequest walletCreateRequest) {
        return HttpResponse.created(walletService.createWallet(walletCreateRequest));
    }

    @Get("/{walletId}/balance")
    public HttpResponse<WalletBalanceResponse> getWalletBalance(@PathVariable UUID walletId) {
        return HttpResponse.ok(walletService.getWalletBalance(walletId));
    }

    @Get("/{walletId}/balance/history")
    public HttpResponse<WalletBalanceResponse> getHistoricalWalletBalance(@PathVariable UUID walletId, @QueryValue OffsetDateTime timestamp) {
        return HttpResponse.ok(walletService.getHistoricalWalletBalance(walletId, timestamp));
    }

    @Retryable
    @Post("/{walletId}/deposit")
    public HttpResponse<WalletBalanceResponse> depositFunds(@PathVariable UUID walletId, @Body @Valid @NotNull TransactionRequest transactionRequest) {
        return HttpResponse.ok(walletService.depositFunds(walletId, transactionRequest));
    }

    @Retryable
    @Post("/{walletId}/withdraw")
    public HttpResponse<WalletBalanceResponse> withdrawFunds(@PathVariable UUID walletId, @Body @Valid @NotNull TransactionRequest transactionRequest) {
        return HttpResponse.ok(walletService.withdrawFunds(walletId, transactionRequest));
    }

    @Retryable
    @Post("/transfer")
    public HttpResponse<WalletBalanceResponse> transferFunds(@Body @Valid @NotNull TransferRequest transferRequest) {
        return HttpResponse.ok(walletService.transferFunds(transferRequest));
    }

    @Error(exception = WalletNotFoundException.class)
    public HttpResponse<WalletBalanceResponse> walletNotFoundHandler(WalletNotFoundException e) {
        return HttpResponse.<WalletBalanceResponse>status(HttpStatus.NOT_FOUND)
                .body(new WalletBalanceResponse(null));
    }
}