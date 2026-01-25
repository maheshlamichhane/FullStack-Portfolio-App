package com.portfolio.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitorAnalyticsRequestDTO {

    private String ipAddress;

    private String userAgent;

    private String referrer;

    private String country;

    private String city;

    private String deviceType; // Desktop, Mobile, Tablet

    private String browser;

    private String operatingSystem;

    @NotBlank(message = "Page visited is required")
    private String pageVisited;

    private LocalDateTime visitedAt;

    private Integer timeSpentSeconds;

    private Boolean isUniqueVisit = true;

    @NotNull(message = "Profile ID is required")
    private Long profileId;
}
