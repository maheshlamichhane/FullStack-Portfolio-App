//package com.portfolio.app.service;
//
//
//import com.portfolio.app.dao.ProfileRepository;
//import com.portfolio.app.dao.TestimonialRepository;
//import com.portfolio.app.dto.TestimonialRequestDTO;
//import com.portfolio.app.dto.TestimonialResponseDTO;
//import com.portfolio.app.dto.TestimonialSummaryDTO;
//import com.portfolio.app.entity.Testimonial;
//import com.portfolio.app.entity.Profile;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class TestimonialServiceImpl implements TestimonialService {
//
//    private final TestimonialRepository testimonialRepository;
//    private final ProfileRepository profileRepository;
////    private final TestimonialMapper testimonialMapper;
//
//    private static final String TESTIMONIAL_NOT_FOUND = "Testimonial not found with id: ";
//    private static final String PROFILE_NOT_FOUND = "Profile not found with id: ";
//
//    @Override
//    public TestimonialResponseDTO createTestimonial(TestimonialRequestDTO testimonialDTO) {
////        Profile profile = profileRepository.findById(testimonialDTO.getProfileId())
////                .orElseThrow(() -> new ResourceNotFoundException(PROFILE_NOT_FOUND + testimonialDTO.getProfileId()));
//
//        Profile profile = null;
//        Testimonial testimonial = testimonialMapper.toEntity(testimonialDTO, profile);
//
//        // Set current date if not provided
//        if (testimonial.getGivenDate() == null) {
//            testimonial.setGivenDate(java.time.LocalDateTime.now());
//        }
//
//        Testimonial savedTestimonial = testimonialRepository.save(testimonial);
//        return testimonialMapper.toDto(savedTestimonial);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public TestimonialResponseDTO getTestimonialById(Long id) {
////        Testimonial testimonial = testimonialRepository.findById(id)
////                .orElseThrow(() -> new ResourceNotFoundException(TESTIMONIAL_NOT_FOUND + id));
////        return testimonialMapper.toDto(testimonial);
//        return null;
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<TestimonialResponseDTO> getAllTestimonials() {
//        return testimonialRepository.findAll().stream()
//                .map(testimonialMapper::toDto)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<TestimonialResponseDTO> getTestimonialsByProfileId(Long profileId) {
//        return testimonialRepository.findByProfileId(profileId).stream()
//                .map(testimonialMapper::toDto)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<TestimonialResponseDTO> getApprovedTestimonialsByProfileId(Long profileId) {
//        return testimonialRepository.findApprovedByProfileId(profileId).stream()
//                .map(testimonialMapper::toDto)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<TestimonialResponseDTO> getFeaturedTestimonials() {
//        return testimonialRepository.findByIsFeaturedTrueAndIsApprovedTrue().stream()
//                .map(testimonialMapper::toDto)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<TestimonialResponseDTO> getFeaturedTestimonialsByProfileId(Long profileId) {
//        return testimonialRepository.findFeaturedByProfileId(profileId).stream()
//                .map(testimonialMapper::toDto)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public TestimonialResponseDTO updateTestimonial(Long id, TestimonialRequestDTO testimonialDTO) {
////        Testimonial existingTestimonial = testimonialRepository.findById(id)
////                .orElseThrow(() -> new ResourceNotFoundException(TESTIMONIAL_NOT_FOUND + id));
////
////        Profile profile = null;
////        if (testimonialDTO.getProfileId() != null) {
////            profile = profileRepository.findById(testimonialDTO.getProfileId())
////                    .orElseThrow(() -> new ResourceNotFoundException(PROFILE_NOT_FOUND + testimonialDTO.getProfileId()));
////        }
//
////        testimonialMapper.updateEntityFromDto(testimonialDTO, existingTestimonial, profile);
////        Testimonial updatedTestimonial = testimonialRepository.save(existingTestimonial);
////        return testimonialMapper.toDto(updatedTestimonial);
//        return null;
//    }
//
//    @Override
//    public void deleteTestimonial(Long id) {
////        if (!testimonialRepository.existsById(id)) {
////            throw new ResourceNotFoundException(TESTIMONIAL_NOT_FOUND + id);
////        }
////        testimonialRepository.deleteById(id);
//    }
//
//    @Override
//    public TestimonialResponseDTO toggleFeaturedStatus(Long id) {
////        Testimonial testimonial = testimonialRepository.findById(id)
////                .orElseThrow(() -> new ResourceNotFoundException(TESTIMONIAL_NOT_FOUND + id));
////
////        testimonial.setIsFeatured(!testimonial.getIsFeatured());
////        Testimonial updatedTestimonial = testimonialRepository.save(testimonial);
////        return testimonialMapper.toDto(updatedTestimonial);
//        return null;
//    }
//
//    @Override
//    public TestimonialResponseDTO toggleApprovalStatus(Long id) {
////        Testimonial testimonial = testimonialRepository.findById(id)
////                .orElseThrow(() -> new ResourceNotFoundException(TESTIMONIAL_NOT_FOUND + id));
////
////        testimonial.setIsApproved(!testimonial.getIsApproved());
////        Testimonial updatedTestimonial = testimonialRepository.save(testimonial);
////        return testimonialMapper.toDto(updatedTestimonial);
//        return null;
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public TestimonialSummaryDTO getTestimonialSummary(Long profileId) {
//        TestimonialSummaryDTO summary = new TestimonialSummaryDTO();
//        summary.setTotalTestimonials(testimonialRepository.countByProfileId(profileId));
//        summary.setApprovedTestimonials(testimonialRepository.countByProfileIdAndIsApprovedTrue(profileId));
//
//        long featuredCount = testimonialRepository.findFeaturedByProfileId(profileId).size();
//        summary.setFeaturedTestimonials(featuredCount);
//
//        testimonialRepository.findAverageRatingByProfileId(profileId)
//                .ifPresent(summary::setAverageRating);
//
//        return summary;
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public Double getAverageRating(Long profileId) {
//        return testimonialRepository.findAverageRatingByProfileId(profileId)
//                .orElse(0.0);
//    }
//}
