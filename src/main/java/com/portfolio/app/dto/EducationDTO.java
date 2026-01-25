package com.portfolio.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

@Data
public class EducationDTO {
    private Long id;

    @NotBlank(message = "Institution is required")
    @Size(min = 2, max = 200, message = "Institution must be between 2 and 200 characters")
    private String institution;

    @NotBlank(message = "Degree is required")
    @Size(min = 2, max = 100, message = "Degree must be between 2 and 100 characters")
    private String degree;

    @Size(max = 100, message = "Field of study cannot exceed 100 characters")
    private String fieldOfStudy;

    private String grade;

    @NotNull(message = "Start date is required")
    @PastOrPresent(message = "Start date must be in the past or present")
    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean isCurrent = false;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    private String location;
    private String institutionLogoUrl;

    @NotNull(message = "Profile ID is required")
    private Long profileId;
}
