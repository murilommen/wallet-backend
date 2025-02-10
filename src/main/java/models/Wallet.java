package models;

import io.micronaut.data.annotation.MappedEntity;
import lombok.*;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "wallets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedEntity
public class Wallet {
    @jakarta.persistence.Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID userId;

    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

//    @DateCreated
    private java.time.Instant createdAt;

//    @DateUpdated
    private java.time.Instant updatedAt;
}
