package com.portfolio.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConfirmSubscriptionDTO {

    @NotBlank(message = "Token is required")
    private String token;

    @NotBlank(message = "Email is required")
    private String email;
}
