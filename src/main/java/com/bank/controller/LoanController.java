package com.bank.controller;

import com.bank.dto.LoanDTO;
import com.bank.dto.LoanRequestDTO;
import com.bank.entity.Customer;
import com.bank.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.bank.repository.CustomerRepository;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class LoanController {

    private final CustomerRepository customerRepository;
    private final LoanService loanService;

    @PostMapping
    public ResponseEntity<LoanDTO> createLoan(@Valid @RequestBody LoanRequestDTO request) {
        log.info("Loan creation request received: amount={}", request.getAmount());

        try {
            Long customerId = getCustomerId();
            LoanDTO response = loanService.createLoan(customerId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Loan creation error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<LoanDTO>> getLoans() {
        log.info("Fetching loans for customer");

        try {
            Long customerId = getCustomerId();
            List<LoanDTO> loans = loanService.getCustomerLoans(customerId);
            return ResponseEntity.ok(loans);
        } catch (Exception e) {
            log.error("Error fetching loans: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanDTO> getLoanDetails(@PathVariable Long id) {
        log.info("Fetching loan details: id={}", id);

        try {
            Long customerId = getCustomerId();
            LoanDTO loan = loanService.getLoanDetails(id, customerId);
            return ResponseEntity.ok(loan);
        } catch (Exception e) {
            log.error("Error fetching loan details: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
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