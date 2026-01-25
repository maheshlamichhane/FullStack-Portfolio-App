package com.portfolio.app.service;

import com.portfolio.app.dto.TestimonialRequestDTO;
import com.portfolio.app.dto.TestimonialResponseDTO;
import com.portfolio.app.dto.TestimonialSummaryDTO;

import java.util.List;

public interface TestimonialService {

    TestimonialResponseDTO createTestimonial(TestimonialRequestDTO testimonialDTO);

    TestimonialResponseDTO getTestimonialById(Long id);

    List<TestimonialResponseDTO> getAllTestimonials();

    List<TestimonialResponseDTO> getTestimonialsByProfileId(Long profileId);

    List<TestimonialResponseDTO> getApprovedTestimonialsByProfileId(Long profileId);

    List<TestimonialResponseDTO> getFeaturedTestimonials();

    List<TestimonialResponseDTO> getFeaturedTestimonialsByProfileId(Long profileId);

    TestimonialResponseDTO updateTestimonial(Long id, TestimonialRequestDTO testimonialDTO);

    void deleteTestimonial(Long id);

    TestimonialResponseDTO toggleFeaturedStatus(Long id);

    TestimonialResponseDTO toggleApprovalStatus(Long id);

    TestimonialSummaryDTO getTestimonialSummary(Long profileId);

    Double getAverageRating(Long profileId);
}
