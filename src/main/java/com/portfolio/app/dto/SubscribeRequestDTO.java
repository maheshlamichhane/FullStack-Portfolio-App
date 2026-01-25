package com.portfolio.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SubscribeRequestDTO {

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @Size(max = 50, message = "Source cannot exceed 50 characters")
    private String source; // Website, Blog, Contact Form

    @NotBlank(message = "Profile identifier is required")
    private String profileIdentifier; // Can be slug, email, or ID
}
