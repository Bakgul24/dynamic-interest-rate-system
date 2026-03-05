package com.bank.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LoanStatus status = LoanStatus.ACTIVE;

    @Column(nullable = false)
    private Integer consecutiveOnTimePayments = 0;

    @Column(nullable = false)
    private Integer consecutiveLatePayments = 0;

    @Column(nullable = false)
    private Double currentRiskScore;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public enum LoanStatus {
        ACTIVE, PAID_OFF, DEFAULT
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.currentRiskScore = 50.0;
    }
}
