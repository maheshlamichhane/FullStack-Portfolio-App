package com.portfolio.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

@Data
public class AchievementDTO {
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 2, max = 200, message = "Title must be between 2 and 200 characters")
    private String title;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @Size(max = 200, message = "Issuing organization cannot exceed 200 characters")
    private String issuingOrganization;

    @NotNull(message = "Date received is required")
    @PastOrPresent(message = "Date received must be in the past or present")
    private LocalDate dateReceived;

    @NotBlank(message = "Category is required")
    @Size(min = 2, max = 50, message = "Category must be between 2 and 50 characters")
    private String category; // Award, Recognition, Publication, etc.

    @Size(max = 500, message = "Link cannot exceed 500 characters")
    private String link;

    private String iconUrl;

    private Boolean isFeatured = false;

    @NotNull(message = "Profile ID is required")
    private Long profileId;
}
