package com.wallet.infrastructure.adapters.web;

import com.wallet.domain.port.incoming.WalletService;
import com.wallet.model.WalletBalanceResponse;
import com.wallet.model.WalletCreateRequest;
import com.wallet.model.WalletResponse;
import com.wallet.model.TransactionRequest;
import com.wallet.model.TransferRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.UUID;

@Controller("/wallets")
public class WalletController {

    @Inject
    WalletService walletService;

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

    @Post("/{walletId}/deposit")
    public HttpResponse<WalletBalanceResponse> depositFunds(@PathVariable UUID walletId, @Body @Valid @NotNull TransactionRequest transactionRequest) {
        return HttpResponse.ok(walletService.depositFunds(walletId, transactionRequest));
    }

    @Post("/{walletId}/withdraw")
    public HttpResponse<WalletBalanceResponse> withdrawFunds(@PathVariable UUID walletId, @Body @Valid @NotNull TransactionRequest transactionRequest) {
        return HttpResponse.ok(walletService.withdrawFunds(walletId, transactionRequest));
    }

    @Post("/transfer")
    public HttpResponse<WalletBalanceResponse> transferFunds(@Body @Valid @NotNull TransferRequest transferRequest) {
        return HttpResponse.ok(walletService.transferFunds(transferRequest));
    }
}