package com.wallet.model;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Value;
import java.math.BigDecimal;

@Serdeable
@Value
public class WalletBalanceResponse {
    BigDecimal balance;
}