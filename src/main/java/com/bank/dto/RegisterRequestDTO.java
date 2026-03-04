package com.bank.dto;

import com.bank.entity.Customer;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequestDTO {

    @NotBlank(message = "National ID cannot be blank")
    @Size(min = 11, max = 11, message = "National ID must be 11 digits")
    private String tcId;

    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Please enter a valid email address")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotNull(message = "Monthly income cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Monthly income must be greater than 0")
    private Double monthlyIncome;

    @NotNull(message = "Employment status cannot be null")
    private Customer.EmploymentStatus employmentStatus;
}