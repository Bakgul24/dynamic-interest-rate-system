package com.bank.service;

import com.bank.dto.AuthResponseDTO;
import com.bank.dto.LoginRequestDTO;
import com.bank.dto.RegisterRequestDTO;
import com.bank.entity.Customer;
import com.bank.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final KYCService kycService;

    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        log.info("Customer registration started: {}", request.getEmail());

        if (kycService.emailExists(request.getEmail())) {
            log.warn("Email already exists: {}", request.getEmail());
            throw new RuntimeException("Email already registered");
        }

        if (kycService.tcIdExists(request.getTcId())) {
            log.warn("TC ID already exists: {}", request.getTcId());
            throw new RuntimeException("TC ID already registered");
        }

        Customer customer = Customer.builder()
                .tcId(request.getTcId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .monthlyIncome(request.getMonthlyIncome())
                .employmentStatus(request.getEmploymentStatus())
                .build();

        customer = customerRepository.save(customer);
        log.info("Customer registered successfully: {} (ID: {})", request.getEmail(), customer.getId());

        String token = jwtService.generateToken(customer.getEmail());

        return AuthResponseDTO.builder()
                .token(token)
                .userId(customer.getId())
                .email(customer.getEmail())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .message("Registered successfully")
                .build();
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        log.info("Customer login attempt: {}", request.getEmail());

        Customer customer = customerRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Customer not found: {}", request.getEmail());
                    return new RuntimeException("Invalid credentials");
                });

        if (!passwordEncoder.matches(request.getPassword(), customer.getPassword())) {
            log.warn("Invalid password for: {}", request.getEmail());
            throw new RuntimeException("Invalid credentials");
        }

        log.info("Customer login successful: {}", request.getEmail());

        String token = jwtService.generateToken(customer.getEmail());

        return AuthResponseDTO.builder()
                .token(token)
                .userId(customer.getId())
                .email(customer.getEmail())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .message("Login successful")
                .build();
    }

    public Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }
}