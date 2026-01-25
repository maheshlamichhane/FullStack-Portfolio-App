package com.portfolio.app.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestimonialSummaryDTO {
    private Long totalTestimonials;
    private Long approvedTestimonials;
    private Long featuredTestimonials;
    private Double averageRating;
}

