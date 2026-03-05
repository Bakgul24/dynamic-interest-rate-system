package com.bank.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDTO {

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Please provide valid email")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    private String password;
}