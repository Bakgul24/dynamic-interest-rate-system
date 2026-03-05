package com.bank.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "risk_assessments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskAssessment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "loan_id")
    private Loan loan;

    @Column(nullable = false)
    private Double totalRiskScore;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RiskCategory riskCategory;

    @Column(nullable = false)
    private Double recommendedInterestRate;

    @Column(nullable = false)
    private LocalDateTime assessmentDate;

    public enum RiskCategory {
        LOW, MEDIUM, HIGH, VERY_HIGH
    }

    @PrePersist
    protected void onCreate() {
        this.assessmentDate = LocalDateTime.now();
    }
}