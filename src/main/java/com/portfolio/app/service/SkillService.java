package com.portfolio.app.service;

import com.portfolio.app.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;

public interface SkillService {

    // Create
    SkillResponseDTO createSkill(SkillDTO skillDTO);

    // Read
    SkillResponseDTO getSkillById(Long id);
    List<SkillResponseDTO> getAllSkillsByProfile(Long profileId);
    Page<SkillResponseDTO> getSkillsByProfile(Long profileId, Pageable pageable);
    List<SkillResponseDTO> getSkillsByCategory(Long profileId, String category);
    List<SkillResponseDTO> getFeaturedSkills(Long profileId);
    List<SkillResponseDTO> getSkillsByProficiencyRange(Long profileId, Integer min, Integer max);
    List<SkillResponseDTO> getTopSkills(Long profileId, int limit);
    List<SkillResponseDTO> searchSkills(Long profileId, String query);
    List<SkillSummaryDTO> getSkillsByDisplayOrder(Long profileId);
    List<SkillCategoryDTO> getSkillsGroupedByCategory(Long profileId);

    // Update
    SkillResponseDTO updateSkill(Long id, SkillDTO skillDTO);
    SkillResponseDTO toggleFeatured(Long id);
    SkillResponseDTO updateDisplayOrder(Long id, Integer displayOrder);
    void updateMultipleDisplayOrders(Map<Long, Integer> displayOrderUpdates);

    // Delete
    void deleteSkill(Long id);
    void deleteAllSkillsByProfile(Long profileId);

    // Statistics
    SkillStatsDTO getSkillStats(Long profileId);
    Map<String, Integer> getProficiencyDistribution(Long profileId);
    List<String> getSkillCategories(Long profileId);
    Integer getTotalYearsOfExperience(Long profileId);

    // Validation
    boolean existsByNameAndProfile(String name, Long profileId);
    boolean isDisplayOrderAvailable(Long profileId, Integer displayOrder, Long excludeId);
}
