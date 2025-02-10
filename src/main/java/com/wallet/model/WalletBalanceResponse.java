package com.wallet.model;

import lombok.Value;
import java.math.BigDecimal;

@Value
public class WalletBalanceResponse {
    BigDecimal balance;
}