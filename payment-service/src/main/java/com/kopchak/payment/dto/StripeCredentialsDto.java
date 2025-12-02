package com.kopchak.payment.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record StripeCredentialsDto(
        @NotBlank(message = "Invalid customer name: customer name is empty")
        String customerName,

        @NotBlank(message = "Invalid email: email is empty")
        @Email(regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+" +
                "(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$",
                message = "Invalid email: email '${validatedValue}' format is incorrect")
        String customerEmail) {
}