package com.bank.service;

import com.bank.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KYCService {

    private final CustomerRepository customerRepository;

    public boolean emailExists(String email) {
        return customerRepository.existsByEmail(email);
    }

    public boolean tcIdExists(String tcId) {
        return customerRepository.existsByTcId(tcId);
    }
}