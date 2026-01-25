package com.portfolio.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestimonialResponseDTO {

    private Long id;
    private String clientName;
    private String clientTitle;
    private String clientCompany;
    private String clientImageUrl;
    private String content;
    private Integer rating;
    private String projectWorkedOn;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime givenDate;

    private Boolean isFeatured;
    private Boolean isApproved;
    private Long profileId;
    private String profileName; // Optional: if you want to include profile info
}
