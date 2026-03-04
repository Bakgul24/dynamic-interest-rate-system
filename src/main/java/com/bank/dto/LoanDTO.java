package com.bank.dto;

import com.bank.entity.Loan;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanDTO {

    private Long id;
    private BigDecimal principalAmount;
    private BigDecimal outstandingBalance;
    private Double currentInterestRate;
    private Double baseInterestRate;
    private Integer remainingMonths;
    private Loan.LoanStatus status;
    private Integer consecutiveOnTimePayments;
    private Integer consecutiveLatePayments;
    private Double currentRiskScore;
    private LocalDateTime createdAt;
}