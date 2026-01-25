package com.portfolio.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "testimonials")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Testimonial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String clientName;

    private String clientTitle;
    private String clientCompany;
    private String clientImageUrl;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(nullable = false)
    private Integer rating; // 1-5

    private String projectWorkedOn;

    @Column(nullable = false)
    private LocalDateTime givenDate = LocalDateTime.now();

    private Boolean isFeatured = false;
    private Boolean isApproved = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;
}
