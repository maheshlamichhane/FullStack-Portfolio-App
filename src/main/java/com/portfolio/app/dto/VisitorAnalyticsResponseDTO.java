package com.portfolio.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitorAnalyticsResponseDTO {

    private Long id;
    private String ipAddress;
    private String userAgent;
    private String referrer;
    private String country;
    private String city;
    private String deviceType;
    private String browser;
    private String operatingSystem;
    private String pageVisited;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime visitedAt;

    private Integer timeSpentSeconds;
    private Boolean isUniqueVisit;
    private Long profileId;
    private String profileName;
}
