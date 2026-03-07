package com.bank.service;

import com.bank.dto.LoanDTO;
import com.bank.dto.LoanRequestDTO;
import com.bank.entity.Customer;
import com.bank.entity.Loan;
import com.bank.repository.CustomerRepository;
import com.bank.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanService {

    private final LoanRepository loanRepository;
    private final CustomerRepository customerRepository;
    private final RiskScoringService riskScoringService;

    @Transactional
    public LoanDTO createLoan(Long customerId, LoanRequestDTO request) {
        log.info("Creating loan for customer ID: {}, Amount: {}", customerId, request.getAmount());

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Double baseRate = 15.;

        var totalAmount = request.getAmount().multiply(BigDecimal.valueOf(baseRate / 100)).add(request.getAmount());

        Loan loan = Loan.builder()
                .customer(customer)
                .principalAmount(request.getAmount())
                .outstandingBalance(totalAmount)
                .baseInterestRate(baseRate)
                .currentInterestRate(baseRate)
                .remainingMonths(request.getDurationMonths())
                .status(Loan.LoanStatus.ACTIVE)
                .consecutiveOnTimePayments(0)
                .consecutiveLatePayments(0)
                .currentRiskScore(50.0)
                .totalPaid(BigDecimal.ZERO)
                .totalDiscount(BigDecimal.ZERO)
                .totalPenalty(BigDecimal.ZERO)
                .build();

        loan = loanRepository.save(loan);
        log.info("Loan created: ID={}, Amount={}", loan.getId(), request.getAmount());

        return convertToDTO(loan);
    }

    public List<LoanDTO> getCustomerLoans(Long customerId) {
        log.info("Fetching loans for customer ID: {}", customerId);

        List<Loan> loans = loanRepository.findByCustomerId(customerId);
        return loans.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public LoanDTO getLoanDetails(Long loanId, Long customerId) {
        log.info("Fetching loan details: loanId={}, customerId={}", loanId, customerId);

        Loan loan = loanRepository.findByIdAndCustomerId(loanId, customerId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        return convertToDTO(loan);
    }

    @Transactional
    public void updateLoanAfterPayment(Loan loan, Boolean isOnTime) {
        log.info("Updating loan after payment: loanId={}, isOnTime={}", loan.getId(), isOnTime);

        if (isOnTime) {
            loan.setConsecutiveOnTimePayments(loan.getConsecutiveOnTimePayments() + 1);
            loan.setConsecutiveLatePayments(0);
        } else {
            loan.setConsecutiveLatePayments(loan.getConsecutiveLatePayments() + 1);
            loan.setConsecutiveOnTimePayments(0);
        }

        Double newRiskScore = riskScoringService.calculateRiskScore(loan);
        loan.setCurrentRiskScore(newRiskScore);

        Double newInterestRate = riskScoringService.calculateDynamicInterestRate(loan);
        loan.setCurrentInterestRate(newInterestRate);

        loan.setRemainingMonths(loan.getRemainingMonths() - 1);

        if (loan.getRemainingMonths() <= 0 && loan.getOutstandingBalance().compareTo(BigDecimal.ZERO) <= 0) {
            loan.setStatus(Loan.LoanStatus.PAID_OFF);
            log.info("Loan paid off: loanId={}", loan.getId());
        }

        loanRepository.save(loan);
        log.info("Loan updated - Risk: {}, InterestRate: {}%", newRiskScore, newInterestRate);
    }

    private LoanDTO convertToDTO(Loan loan) {
        BigDecimal totalInterest = loan.getPrincipalAmount()
                .multiply(BigDecimal.valueOf(loan.getBaseInterestRate() / 100));

        BigDecimal bankProfit = totalInterest
                .subtract(loan.getTotalDiscount())
                .add(loan.getTotalPenalty());

        return LoanDTO.builder()
                .id(loan.getId())
                .principalAmount(loan.getPrincipalAmount())
                .outstandingBalance(loan.getOutstandingBalance())
                .currentInterestRate(loan.getCurrentInterestRate())
                .baseInterestRate(loan.getBaseInterestRate())
                .remainingMonths(loan.getRemainingMonths())
                .status(loan.getStatus())
                .consecutiveOnTimePayments(loan.getConsecutiveOnTimePayments())
                .consecutiveLatePayments(loan.getConsecutiveLatePayments())
                .currentRiskScore(loan.getCurrentRiskScore())
                .createdAt(loan.getCreatedAt())
                .totalPaid(loan.getTotalPaid())
                .totalDiscount(loan.getTotalDiscount())
                .totalPenalty(loan.getTotalPenalty())
                .bankProfit(bankProfit)
                .build();
    }
}