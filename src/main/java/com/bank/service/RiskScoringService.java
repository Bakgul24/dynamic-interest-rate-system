package com.bank.service;

import com.bank.entity.Loan;
import com.bank.entity.RiskAssessment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiskScoringService {

    public Double calculateRiskScore(Loan loan) {
        Double riskScore = 50.0;

        if (loan.getConsecutiveOnTimePayments() >= 6) {
            riskScore -= 20;
        } else if (loan.getConsecutiveOnTimePayments() >= 3) {
            riskScore -= 10;
        } else if (loan.getConsecutiveOnTimePayments() > 0) {
            riskScore -= 5;
        }

        if (loan.getConsecutiveLatePayments() >= 3) {
            riskScore += 25;
        } else if (loan.getConsecutiveLatePayments() >= 1) {
            riskScore += 15;
        }

        riskScore = Math.max(0, Math.min(100, riskScore));

        log.info("Risk score calculated for loan {}: {}", loan.getId(), riskScore);
        return riskScore;
    }

    public Double calculateDynamicInterestRate(Loan loan) {
        Double baseRate = loan.getBaseInterestRate();
        Double riskScore = loan.getCurrentRiskScore();

        Double additionalRate = (riskScore / 100) * 20;
        Double newRate = baseRate + additionalRate;

        if (loan.getConsecutiveOnTimePayments() >= 3) {
            newRate = Math.max(baseRate, newRate - 0.5);
        }

        if (loan.getConsecutiveLatePayments() > 0) {
            newRate += (loan.getConsecutiveLatePayments() * 1.5);
        }

        log.info("Interest rate calculated for loan {}: {}% (base: {}%, risk: {})",
                loan.getId(), newRate, baseRate, riskScore);

        return newRate;
    }

    public RiskAssessment.RiskCategory getRiskCategory(Double riskScore) {
        if (riskScore <= 25) {
            return RiskAssessment.RiskCategory.LOW;
        } else if (riskScore <= 50) {
            return RiskAssessment.RiskCategory.MEDIUM;
        } else if (riskScore <= 75) {
            return RiskAssessment.RiskCategory.HIGH;
        } else {
            return RiskAssessment.RiskCategory.VERY_HIGH;
        }
    }
}