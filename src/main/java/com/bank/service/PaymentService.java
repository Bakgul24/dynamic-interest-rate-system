package com.bank.service;

import com.bank.dto.PaymentDTO;
import com.bank.dto.PaymentRequestDTO;
import com.bank.entity.Customer;
import com.bank.entity.Loan;
import com.bank.entity.Payment;
import com.bank.repository.CustomerRepository;
import com.bank.repository.LoanRepository;
import com.bank.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final LoanRepository loanRepository;
    private final CustomerRepository customerRepository;
    private final LoanService loanService;

    @Transactional
    public PaymentDTO makePayment(Long customerId, PaymentRequestDTO request) {
        log.info("Payment processing: customerId={}, loanId={}, amount={}",
                customerId, request.getLoanId(), request.getAmount());

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Loan loan = loanRepository.findByIdAndCustomerId(request.getLoanId(), customerId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        if (!loan.getStatus().equals(Loan.LoanStatus.ACTIVE)) {
            throw new RuntimeException("Loan is not active");
        }

        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Payment amount must be greater than 0");
        }

        Boolean isOnTime = true;

        Payment payment = Payment.builder()
                .loan(loan)
                .customer(customer)
                .amount(request.getAmount())
                .isOnTime(isOnTime)
                .paymentDate(LocalDateTime.now())
                .status(Payment.PaymentStatus.SUCCESSFUL)
                .paymentReference(request.getPaymentReference())
                .build();

        payment = paymentRepository.save(payment);
        log.info("Payment saved: ID={}, Amount={}", payment.getId(), request.getAmount());

        BigDecimal newBalance = loan.getOutstandingBalance().subtract(request.getAmount());

        loan.setTotalPaid(loan.getTotalPaid().add(request.getAmount()));

        if (isOnTime) {
            BigDecimal cashbackAmount = request.getAmount()
                    .multiply(BigDecimal.valueOf(0.02));
            newBalance = newBalance.subtract(cashbackAmount);

            loan.setTotalDiscount(loan.getTotalDiscount().add(cashbackAmount));

            log.info("On-time payment bonus: {} × 3% = {}",
                    request.getAmount(), cashbackAmount);
        }

        if (loan.getConsecutiveLatePayments() > 0) {
            Double latePenalty = loan.getConsecutiveLatePayments() * 0.02;
            BigDecimal penaltyAmount = newBalance
                    .multiply(BigDecimal.valueOf(latePenalty));
            newBalance = newBalance.add(penaltyAmount);

            loan.setTotalPenalty(loan.getTotalPenalty().add(penaltyAmount));

            log.info("Late payment penalty: {} late × 2% = {}",
                    loan.getConsecutiveLatePayments(), penaltyAmount);
        }

        if (newBalance.compareTo(BigDecimal.ZERO) <= 0) {
            loan.setStatus(Loan.LoanStatus.PAID_OFF);
            newBalance = BigDecimal.ZERO;
            log.info("Loan paid off: {}", loan.getId());
        }

        loan.setOutstandingBalance(newBalance);
        loanService.updateLoanAfterPayment(loan, isOnTime);

        log.info("Payment completed successfully: paymentId={}", payment.getId());

        return convertToDTO(payment);
    }

    public List<PaymentDTO> getPaymentHistory(Long loanId, Long customerId) {
        log.info("Fetching payment history: loanId={}", loanId);

        loanRepository.findByIdAndCustomerId(loanId, customerId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        List<Payment> payments = paymentRepository.findByLoanId(loanId);
        return payments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PaymentDTO> getCustomerPayments(Long customerId) {
        log.info("Fetching all payments for customer: {}", customerId);

        List<Payment> payments = paymentRepository.findByCustomerId(customerId);
        return payments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private PaymentDTO convertToDTO(Payment payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
                .loanId(payment.getLoan().getId())
                .amount(payment.getAmount())
                .isOnTime(payment.getIsOnTime())
                .paymentDate(payment.getPaymentDate())
                .status(payment.getStatus())
                .paymentReference(payment.getPaymentReference())
                .build();
    }
}