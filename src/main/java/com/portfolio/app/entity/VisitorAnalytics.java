package com.portfolio.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "visitor_analytics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitorAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ipAddress;
    private String userAgent;
    private String referrer;
    private String country;
    private String city;
    private String deviceType; // Desktop, Mobile, Tablet
    private String browser;
    private String operatingSystem;

    @Column(nullable = false)
    private String pageVisited;

    @Column(nullable = false)
    private LocalDateTime visitedAt = LocalDateTime.now();

    private Integer timeSpentSeconds;
    private Boolean isUniqueVisit = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;
}
