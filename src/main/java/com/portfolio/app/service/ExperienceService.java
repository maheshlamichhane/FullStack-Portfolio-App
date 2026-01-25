package com.portfolio.app.service;

import com.portfolio.app.dto.ExperienceDTO;
import com.portfolio.app.dto.ExperienceResponseDTO;
import com.portfolio.app.dto.ExperienceSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;

public interface ExperienceService {

    // Create
    ExperienceResponseDTO createExperience(ExperienceDTO experienceDTO);

    // Read
    ExperienceResponseDTO getExperienceById(Long id);
    List<ExperienceResponseDTO> getAllExperiencesByProfile(Long profileId);
    Page<ExperienceResponseDTO> getExperiencesByProfile(Long profileId, Pageable pageable);
    List<ExperienceResponseDTO> getCurrentExperiences(Long profileId);
    List<ExperienceResponseDTO> getPastExperiences(Long profileId);
    List<ExperienceResponseDTO> getExperiencesByType(Long profileId, String employmentType);
    List<ExperienceResponseDTO> searchExperiencesByCompany(Long profileId, String company);
    List<ExperienceResponseDTO> getExperiencesByTechnology(Long profileId, String technology);
    List<ExperienceSummaryDTO> getRecentExperiences(Long profileId, int limit);

    // Update
    ExperienceResponseDTO updateExperience(Long id, ExperienceDTO experienceDTO);

    // Delete
    void deleteExperience(Long id);
    void deleteAllExperiencesByProfile(Long profileId);

    // Statistics
    Map<String, Object> getExperienceStats(Long profileId);
    Integer calculateTotalExperienceYears(Long profileId);
    Map<String, Long> getEmploymentTypeDistribution(Long profileId);

    // Validation
    boolean hasDateOverlap(Long profileId, Long excludeId,
                           String startDate, String endDate, Boolean isCurrent);
}
