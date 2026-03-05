package com.bank.controller;

import com.bank.dto.PaymentDTO;
import com.bank.dto.PaymentRequestDTO;
import com.bank.entity.Customer;
import com.bank.repository.CustomerRepository;
import com.bank.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PaymentController {

    private final CustomerRepository customerRepository;
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentDTO> makePayment(@Valid @RequestBody PaymentRequestDTO request) {
        log.info("Payment request received: loanId={}, amount={}", request.getLoanId(), request.getAmount());

        try {
            Long customerId = getCustomerId();
            PaymentDTO response = paymentService.makePayment(customerId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Payment error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(PaymentDTO.builder()
                            .build());
        }
    }

    @GetMapping("/loan/{loanId}")
    public ResponseEntity<List<PaymentDTO>> getPaymentHistory(@PathVariable Long loanId) {
        log.info("Fetching payment history for loan: {}", loanId);

        try {
            Long customerId = getCustomerId();
            List<PaymentDTO> payments = paymentService.getPaymentHistory(loanId, customerId);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            log.error("Error fetching payment history: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<PaymentDTO>> getCustomerPayments() {
        log.info("Fetching all payments for customer");

        try {
            Long customerId = getCustomerId();
            List<PaymentDTO> payments = paymentService.getCustomerPayments(customerId);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            log.error("Error fetching payments: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private Long getCustomerId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new RuntimeException("Not authenticated");
        }

        String email = auth.getName();

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return customer.getId();
    }
}