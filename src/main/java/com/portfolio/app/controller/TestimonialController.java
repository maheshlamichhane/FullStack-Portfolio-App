//package com.portfolio.app.controller;
//
//
//import com.portfolio.app.dto.TestimonialRequestDTO;
//import com.portfolio.app.dto.TestimonialResponseDTO;
//import com.portfolio.app.dto.TestimonialSummaryDTO;
//import com.portfolio.app.service.TestimonialService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/testimonials")
//@RequiredArgsConstructor
//public class TestimonialController {
//
//    private final TestimonialService testimonialService;
//
//    @PostMapping
//    public ResponseEntity<TestimonialResponseDTO> createTestimonial(
//            @Valid @RequestBody TestimonialRequestDTO testimonialDTO) {
//        TestimonialResponseDTO createdTestimonial = testimonialService.createTestimonial(testimonialDTO);
//        return new ResponseEntity<>(createdTestimonial, HttpStatus.CREATED);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<TestimonialResponseDTO> getTestimonialById(@PathVariable Long id) {
//        TestimonialResponseDTO testimonial = testimonialService.getTestimonialById(id);
//        return ResponseEntity.ok(testimonial);
//    }
//
//    @GetMapping
//    public ResponseEntity<List<TestimonialResponseDTO>> getAllTestimonials() {
//        List<TestimonialResponseDTO> testimonials = testimonialService.getAllTestimonials();
//        return ResponseEntity.ok(testimonials);
//    }
//
//    @GetMapping("/profile/{profileId}")
//    public ResponseEntity<List<TestimonialResponseDTO>> getTestimonialsByProfileId(@PathVariable Long profileId) {
//        List<TestimonialResponseDTO> testimonials = testimonialService.getTestimonialsByProfileId(profileId);
//        return ResponseEntity.ok(testimonials);
//    }
//
//    @GetMapping("/profile/{profileId}/approved")
//    public ResponseEntity<List<TestimonialResponseDTO>> getApprovedTestimonialsByProfileId(@PathVariable Long profileId) {
//        List<TestimonialResponseDTO> testimonials = testimonialService.getApprovedTestimonialsByProfileId(profileId);
//        return ResponseEntity.ok(testimonials);
//    }
//
//    @GetMapping("/featured")
//    public ResponseEntity<List<TestimonialResponseDTO>> getFeaturedTestimonials() {
//        List<TestimonialResponseDTO> testimonials = testimonialService.getFeaturedTestimonials();
//        return ResponseEntity.ok(testimonials);
//    }
//
//    @GetMapping("/profile/{profileId}/featured")
//    public ResponseEntity<List<TestimonialResponseDTO>> getFeaturedTestimonialsByProfileId(@PathVariable Long profileId) {
//        List<TestimonialResponseDTO> testimonials = testimonialService.getFeaturedTestimonialsByProfileId(profileId);
//        return ResponseEntity.ok(testimonials);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<TestimonialResponseDTO> updateTestimonial(
//            @PathVariable Long id,
//            @Valid @RequestBody TestimonialRequestDTO testimonialDTO) {
//        TestimonialResponseDTO updatedTestimonial = testimonialService.updateTestimonial(id, testimonialDTO);
//        return ResponseEntity.ok(updatedTestimonial);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteTestimonial(@PathVariable Long id) {
//        testimonialService.deleteTestimonial(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    @PatchMapping("/{id}/toggle-featured")
//    public ResponseEntity<TestimonialResponseDTO> toggleFeaturedStatus(@PathVariable Long id) {
//        TestimonialResponseDTO updatedTestimonial = testimonialService.toggleFeaturedStatus(id);
//        return ResponseEntity.ok(updatedTestimonial);
//    }
//
//    @PatchMapping("/{id}/toggle-approval")
//    public ResponseEntity<TestimonialResponseDTO> toggleApprovalStatus(@PathVariable Long id) {
//        TestimonialResponseDTO updatedTestimonial = testimonialService.toggleApprovalStatus(id);
//        return ResponseEntity.ok(updatedTestimonial);
//    }
//
//    @GetMapping("/profile/{profileId}/summary")
//    public ResponseEntity<TestimonialSummaryDTO> getTestimonialSummary(@PathVariable Long profileId) {
//        TestimonialSummaryDTO summary = testimonialService.getTestimonialSummary(profileId);
//        return ResponseEntity.ok(summary);
//    }
//
//    @GetMapping("/profile/{profileId}/average-rating")
//    public ResponseEntity<Double> getAverageRating(@PathVariable Long profileId) {
//        Double averageRating = testimonialService.getAverageRating(profileId);
//        return ResponseEntity.ok(averageRating);
//    }
//}
