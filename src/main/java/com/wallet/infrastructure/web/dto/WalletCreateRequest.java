package com.wallet.infrastructure.web.dto;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Serdeable
public class WalletCreateRequest {
    @NotNull
    private UUID userId;
}