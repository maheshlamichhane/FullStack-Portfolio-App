package com.portfolio.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SubscriberDTO {
    private Long id;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    private LocalDateTime subscribedAt;

    private LocalDateTime unsubscribedAt;

    private Boolean isActive = true;

    @Size(max = 50, message = "Subscription source cannot exceed 50 characters")
    private String subscriptionSource; // Website, Blog, Contact Form

    private String confirmationToken;

    private Boolean isConfirmed = false;

    @NotNull(message = "Profile ID is required")
    private Long profileId;
}
