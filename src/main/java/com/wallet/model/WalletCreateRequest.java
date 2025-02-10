package com.wallet.model;

import lombok.Value;
import java.util.UUID;

@Value
public class WalletCreateRequest {
    UUID userId;
}
