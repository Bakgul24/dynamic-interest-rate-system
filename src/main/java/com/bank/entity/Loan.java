package com.bank.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private BigDecimal principalAmount;

    @Column(nullable = false)
    private BigDecimal outstandingBalance;

    @Column(nullable = false)
    private Double currentInterestRate;

    @Column(nullable = false)
    private Double baseInterestRate;

    @Column(nullable = false)
    private Integer remainingMonths;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status;

    @Column(nullable = false)
    private Integer consecutiveOnTimePayments;

    @Column(nullable = false)
    private Integer consecutiveLatePayments;

    @Column(nullable = false)
    private Double currentRiskScore;

    @Column(nullable = false)
    private BigDecimal totalPaid = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal totalDiscount = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal totalPenalty = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum LoanStatus {
        ACTIVE, PAID_OFF, DEFAULT
    }
}