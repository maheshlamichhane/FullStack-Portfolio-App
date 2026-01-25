package com.portfolio.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class ExperienceDTO {
    private Long id;

    @NotBlank(message = "Company name is required")
    @Size(min = 2, max = 100, message = "Company name must be between 2 and 100 characters")
    private String company;

    @NotBlank(message = "Position is required")
    @Size(min = 2, max = 100, message = "Position must be between 2 and 100 characters")
    private String position;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    private String location;

    @NotBlank(message = "Employment type is required")
    private String employmentType; // FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP

    @NotNull(message = "Start date is required")
    @PastOrPresent(message = "Start date must be in the past or present")
    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean isCurrent = false;

    @Size(max = 20, message = "Maximum 20 technologies allowed")
    private List<String> technologies;

    @NotNull(message = "Profile ID is required")
    private Long profileId;
}
