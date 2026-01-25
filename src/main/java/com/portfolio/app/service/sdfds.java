package com.portfolio.app.service;

import com.portfolio.app.dao.ExperienceRepository;
import com.portfolio.app.dao.ProfileRepository;
import com.portfolio.app.dto.*;
import com.portfolio.app.entity.Experience;
import com.portfolio.app.entity.Profile;
import com.portfolio.app.exception.ResourceNotFoundException;
import com.portfolio.app.exception.BusinessRuleException;
import com.portfolio.app.mapper.ExperienceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExperienceServiceImpl implements ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final ProfileRepository profileRepository;
    private final ExperienceMapper experienceMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    @Transactional
    public ExperienceResponseDTO createExperience(ExperienceDTO experienceDTO) {
        log.info("Creating new experience for profile {}: {} at {}",
                experienceDTO.getProfileId(), experienceDTO.getPosition(), experienceDTO.getCompany());

        // Validate profile exists
        Profile profile = profileRepository.findById(experienceDTO.getProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "id", experienceDTO.getProfileId()));

        // Validate dates
        validateExperienceDates(experienceDTO);

        // Check for date overlaps
        if (hasDateOverlap(experienceDTO.getProfileId(), null,
                experienceDTO.getStartDate().toString(),
                experienceDTO.getIsCurrent() ? null : experienceDTO.getEndDate().toString(),
                experienceDTO.getIsCurrent())) {
            throw new BusinessRuleException("Experience dates overlap with existing experience");
        }

        Experience experience = experienceMapper.toEntity(experienceDTO);
        experience.setProfile(profile);

        Experience savedExperience = experienceRepository.save(experience);
        log.info("Experience created successfully with ID: {}", savedExperience.getId());

        return experienceMapper.toResponseDto(savedExperience);
    }

    @Override
    @Transactional(readOnly = true)
    public ExperienceResponseDTO getExperienceById(Long id) {
        log.debug("Fetching experience by ID: {}", id);

        Experience experience = experienceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Experience", "id", id));

        return experienceMapper.toResponseDto(experience);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExperienceResponseDTO> getAllExperiencesByProfile(Long profileId) {
        log.debug("Fetching all experiences for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return experienceRepository.findByProfileId(profileId)
                .stream()
                .map(experienceMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExperienceResponseDTO> getExperiencesByProfile(Long profileId, Pageable pageable) {
        log.debug("Fetching experiences for profile ID: {} with pagination", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return experienceRepository.findByProfileId(profileId, pageable)
                .map(experienceMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExperienceResponseDTO> getCurrentExperiences(Long profileId) {
        log.debug("Fetching current experiences for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return experienceRepository.findByProfileIdAndIsCurrentTrue(profileId)
                .stream()
                .map(experienceMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExperienceResponseDTO> getPastExperiences(Long profileId) {
        log.debug("Fetching past experiences for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return experienceRepository.findByProfileIdAndIsCurrentFalse(profileId)
                .stream()
                .map(experienceMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExperienceResponseDTO> getExperiencesByType(Long profileId, String employmentType) {
        log.debug("Fetching experiences of type {} for profile ID: {}", employmentType, profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return experienceRepository.findByProfileIdAndEmploymentType(profileId, employmentType)
                .stream()
                .map(experienceMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExperienceResponseDTO> searchExperiencesByCompany(Long profileId, String company) {
        log.debug("Searching experiences by company '{}' for profile ID: {}", company, profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        if (company == null || company.trim().isEmpty()) {
            return getAllExperiencesByProfile(profileId);
        }

        return experienceRepository.findByProfileIdAndCompanyContaining(profileId, company.trim())
                .stream()
                .map(experienceMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExperienceResponseDTO> getExperiencesByTechnology(Long profileId, String technology) {
        log.debug("Fetching experiences with technology '{}' for profile ID: {}", technology, profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        if (technology == null || technology.trim().isEmpty()) {
            return getAllExperiencesByProfile(profileId);
        }

        return experienceRepository.findByProfileIdAndTechnology(profileId, technology.trim())
                .stream()
                .map(experienceMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExperienceSummaryDTO> getRecentExperiences(Long profileId, int limit) {
        log.debug("Fetching {} most recent experiences for profile ID: {}", limit, profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        Pageable pageable = PageRequest.of(0, limit);
        return experienceRepository.findByProfileIdOrderByStartDateDesc(profileId, pageable)
                .stream()
                .map(experienceMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ExperienceResponseDTO updateExperience(Long id, ExperienceDTO experienceDTO) {
        log.info("Updating experience with ID: {}", id);

        Experience existingExperience = experienceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Experience", "id", id));

        // Validate dates
        validateExperienceDates(experienceDTO);

        // Check for date overlaps (excluding current experience)
        if (hasDateOverlap(experienceDTO.getProfileId(), id,
                experienceDTO.getStartDate().toString(),
                experienceDTO.getIsCurrent() ? null : experienceDTO.getEndDate().toString(),
                experienceDTO.getIsCurrent())) {
            throw new BusinessRuleException("Experience dates overlap with existing experience");
        }

        // Update profile if changed
        if (!existingExperience.getProfile().getId().equals(experienceDTO.getProfileId())) {
            Profile profile = profileRepository.findById(experienceDTO.getProfileId())
                    .orElseThrow(() -> new ResourceNotFoundException("Profile", "id", experienceDTO.getProfileId()));
            existingExperience.setProfile(profile);
        }

        experienceMapper.updateEntityFromDto(experienceDTO, existingExperience);

        Experience updatedExperience = experienceRepository.save(existingExperience);
        log.info("Experience updated successfully: {}", id);

        return experienceMapper.toResponseDto(updatedExperience);
    }

    @Override
    @Transactional
    public void deleteExperience(Long id) {
        log.info("Deleting experience with ID: {}", id);

        if (!experienceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Experience", "id", id);
        }

        experienceRepository.deleteById(id);
        log.info("Experience deleted: {}", id);
    }

    @Override
    @Transactional
    public void deleteAllExperiencesByProfile(Long profileId) {
        log.info("Deleting all experiences for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        experienceRepository.deleteByProfileId(profileId);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getExperienceStats(Long profileId) {
        log.debug("Getting experience statistics for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        Map<String, Object> stats = new HashMap<>();

        // Get all experiences
        List<Experience> experiences = experienceRepository.findByProfileId(profileId);

        // Basic counts
        long totalExperiences = experiences.size();
        long currentExperiences = experiences.stream()
                .filter(e -> Boolean.TRUE.equals(e.getIsCurrent()))
                .count();
        long pastExperiences = totalExperiences - currentExperiences;

        // Calculate total experience in years
        Integer totalMonths = experienceRepository.calculateTotalExperienceMonths(profileId);
        double totalYears = totalMonths != null ? totalMonths / 12.0 : 0;

        // Get companies worked at
        Set<String> companies = experiences.stream()
                .map(Experience::getCompany)
                .collect(Collectors.toSet());

        // Get unique technologies
        Set<String> technologies = experiences.stream()
                .filter(e -> e.getTechnologies() != null)
                .flatMap(e -> e.getTechnologies().stream())
                .collect(Collectors.toSet());

        // Get employment type distribution
        Map<String, Long> employmentTypeDistribution = getEmploymentTypeDistribution(profileId);

        // Get timeline data
        List<Map<String, Object>> timeline = experiences.stream()
                .sorted(Comparator.comparing(Experience::getStartDate).reversed())
                .map(e -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", e.getId());
                    item.put("company", e.getCompany());
                    item.put("position", e.getPosition());
                    item.put("startDate", e.getStartDate());
                    item.put("endDate", e.getEndDate());
                    item.put("isCurrent", e.getIsCurrent());
                    item.put("durationMonths", calculateDurationMonths(e.getStartDate(),
                            e.getEndDate(), e.getIsCurrent()));
                    return item;
                })
                .collect(Collectors.toList());

        stats.put("totalExperiences", totalExperiences);
        stats.put("currentExperiences", currentExperiences);
        stats.put("pastExperiences", pastExperiences);
        stats.put("totalYears", Math.round(totalYears * 100.0) / 100.0); // Rounded to 2 decimals
        stats.put("companiesCount", companies.size());
        stats.put("technologiesCount", technologies.size());
        stats.put("employmentTypeDistribution", employmentTypeDistribution);
        stats.put("timeline", timeline);

        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public Integer calculateTotalExperienceYears(Long profileId) {
        Integer totalMonths = experienceRepository.calculateTotalExperienceMonths(profileId);
        return totalMonths != null ? totalMonths / 12 : 0;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getEmploymentTypeDistribution(Long profileId) {
        List<Experience> experiences = experienceRepository.findByProfileId(profileId);

        return experiences.stream()
                .collect(Collectors.groupingBy(
                        Experience::getEmploymentType,
                        Collectors.counting()
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasDateOverlap(Long profileId, Long excludeId,
                                  String startDateStr, String endDateStr, Boolean isCurrent) {
        try {
            LocalDate startDate = LocalDate.parse(startDateStr, DATE_FORMATTER);
            LocalDate endDate = endDateStr != null ? LocalDate.parse(endDateStr, DATE_FORMATTER) : null;

            List<Experience> overlapping = experienceRepository.findOverlappingExperiences(
                    profileId, excludeId != null ? excludeId : -1L,
                    startDate, endDate != null ? endDate : LocalDate.now(),
                    isCurrent
            );

            return !overlapping.isEmpty();
        } catch (DateTimeParseException e) {
            log.error("Invalid date format: {}", e.getMessage());
            return false;
        }
    }

    private void validateExperienceDates(ExperienceDTO experienceDTO) {
        if (experienceDTO.getStartDate() == null) {
            throw new BusinessRuleException("Start date is required");
        }

        if (Boolean.FALSE.equals(experienceDTO.getIsCurrent()) && experienceDTO.getEndDate() == null) {
            throw new BusinessRuleException("End date is required for past experiences");
        }

        if (experienceDTO.getEndDate() != null && experienceDTO.getStartDate().isAfter(experienceDTO.getEndDate())) {
            throw new BusinessRuleException("Start date cannot be after end date");
        }

        if (experienceDTO.getEndDate() != null && experienceDTO.getEndDate().isAfter(LocalDate.now())) {
            throw new BusinessRuleException("End date cannot be in the future");
        }

        if (experienceDTO.getStartDate().isAfter(LocalDate.now())) {
            throw new BusinessRuleException("Start date cannot be in the future");
        }
    }

    private Integer calculateDurationMonths(LocalDate startDate, LocalDate endDate, Boolean isCurrent) {
        if (startDate == null) return 0;

        LocalDate end = (isCurrent != null && isCurrent) ? LocalDate.now() : endDate;
        if (end == null) return 0;

        return (int) java.time.temporal.ChronoUnit.MONTHS.between(
                startDate.withDayOfMonth(1),
                end.withDayOfMonth(1)
        );
    }
}
