package com.bank.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestDTO {

    @NotNull(message = "Loan ID cannot be null")
    private Long loanId;

    @NotNull(message = "Payment amount cannot be null")
    @DecimalMin(value = "1", message = "Payment amount must be greater than 1 TL")
    private BigDecimal amount;

    @NotBlank(message = "Payment reference cannot be blank")
    @Size(min = 4, max = 20, message = "Reference must be between 4 and 20 characters")
    private String paymentReference;
}