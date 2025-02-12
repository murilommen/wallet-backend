package com.wallet.infrastructure.web.dto;

import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Serdeable
@AllArgsConstructor
public class WalletCreateRequest {
    @NotNull
    private UUID userId;
}