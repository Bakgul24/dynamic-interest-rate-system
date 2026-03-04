package com.bank.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanRequestDTO {

    @NotNull(message = "Loan amount cannot be empty")
    @DecimalMin(value = "1000", message = "Minimum loan amount is 1000 TL")
    @DecimalMax(value = "1000000", message = "Maximum loan amount is 1,000,000 TL")
    private BigDecimal amount;

    @NotNull(message = "Loan duration cannot be empty")
    @Min(value = 6, message = "Minimum 6 months")
    @Max(value = 60, message = "Maximum 60 months")
    private Integer durationMonths;
}