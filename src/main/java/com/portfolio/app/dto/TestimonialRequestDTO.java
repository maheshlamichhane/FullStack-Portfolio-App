package com.portfolio.app.dto;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestimonialRequestDTO {

    @NotBlank(message = "Client name is required")
    @Size(min = 2, max = 100, message = "Client name must be between 2 and 100 characters")
    private String clientName;

    @Size(max = 100, message = "Client title must not exceed 100 characters")
    private String clientTitle;

    @Size(max = 100, message = "Client company must not exceed 100 characters")
    private String clientCompany;

    private String clientImageUrl;

    @NotBlank(message = "Testimonial content is required")
    @Size(min = 10, max = 2000, message = "Content must be between 10 and 2000 characters")
    private String content;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must not exceed 5")
    private Integer rating;

    @Size(max = 200, message = "Project name must not exceed 200 characters")
    private String projectWorkedOn;

    private LocalDateTime givenDate;

    private Boolean isFeatured = false;

    private Boolean isApproved = true;

    @NotNull(message = "Profile ID is required")
    private Long profileId;
}
