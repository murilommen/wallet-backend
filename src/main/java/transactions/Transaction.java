//package transactions;
//
//import io.micronaut.data.annotation.*;
//import lombok.*;
//
//import jakarta.persistence.*;
//import models.Wallet;
//import types.TransactionType;
//
//import java.math.BigDecimal;
//import java.util.UUID;
//
//@Entity
//@Table(name = "transactions")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@MappedEntity
//public class Transaction {
//    @Id
//    @GeneratedValue
//    private UUID id;
//
//    @ManyToOne
//    @JoinColumn(name = "wallet_id", nullable = false)
//    private Wallet wallet;
//
//    @Column(nullable = false)
//    private BigDecimal amount;
//
//    @Column(nullable = false)
//    @Enumerated(EnumType.STRING)
//    private TransactionType transactionType;
//
//    @DateCreated
//    private java.time.Instant createdAt;
//}
