package com.portfolio.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@SuperBuilder
public class CertificationDTO {
    private Long id;

    @NotBlank(message = "Certification name is required")
    @Size(min = 2, max = 200, message = "Certification name must be between 2 and 200 characters")
    private String name;

    @NotBlank(message = "Issuing organization is required")
    @Size(min = 2, max = 200, message = "Issuing organization must be between 2 and 200 characters")
    private String issuingOrganization;

    @Size(max = 50, message = "Credential ID cannot exceed 50 characters")
    private String credentialId;

    private String credentialUrl;

    @NotNull(message = "Issue date is required")
    @PastOrPresent(message = "Issue date must be in the past or present")
    private LocalDate issueDate;

    private LocalDate expirationDate;

    private Boolean doesNotExpire = false;
    private Boolean isVerified = false;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @Size(max = 500, message = "Skills cannot exceed 500 characters")
    private String skills;

    private String logoUrl;

    @NotNull(message = "Profile ID is required")
    private Long profileId;
}
