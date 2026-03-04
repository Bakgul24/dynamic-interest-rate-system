package com.bank.dto;

import com.bank.entity.Payment;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDTO {

    private Long id;
    private Long loanId;
    private BigDecimal amount;
    private Boolean isOnTime;
    private LocalDateTime paymentDate;
    private Payment.PaymentStatus status;
    private String paymentReference;
}