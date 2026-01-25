package com.portfolio.app.service;

import com.portfolio.app.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;

public interface AchievementService {

    // Create
    AchievementResponseDTO createAchievement(AchievementDTO achievementDTO);

    // Read
    AchievementResponseDTO getAchievementById(Long id);
    List<AchievementResponseDTO> getAllAchievementsByProfile(Long profileId);
    Page<AchievementResponseDTO> getAchievementsByProfile(Long profileId, Pageable pageable);
    List<AchievementResponseDTO> getAchievementsByCategory(Long profileId, String category);
    List<AchievementResponseDTO> getFeaturedAchievements(Long profileId);
    List<AchievementResponseDTO> getAchievementsByOrganization(Long profileId, String organization);
    List<AchievementResponseDTO> getAchievementsByYear(Long profileId, Integer year);
    List<AchievementResponseDTO> getRecentAchievements(Long profileId, int limit);
    List<AchievementResponseDTO> searchAchievements(Long profileId, String query);
    List<AchievementCategoryDTO> getAchievementsGroupedByCategory(Long profileId);
    List<AchievementTimelineDTO> getAchievementsTimeline(Long profileId);
    AchievementStatsDTO getAchievementStats(Long profileId);

    // Update
    AchievementResponseDTO updateAchievement(Long id, AchievementDTO achievementDTO);
    AchievementResponseDTO toggleFeatured(Long id);

    // Delete
    void deleteAchievement(Long id);
    void deleteAllAchievementsByProfile(Long profileId);
    void deleteAchievementsByCategory(Long profileId, String category);

    // Statistics & Analytics
    Map<String, Long> getCategoryDistribution(Long profileId);
    Map<Integer, Long> getYearlyDistribution(Long profileId);
    List<String> getDistinctCategories(Long profileId);
    List<String> getDistinctOrganizations(Long profileId);
    Integer getTotalAchievementYears(Long profileId);
    AchievementResponseDTO getLatestAchievement(Long profileId);

    // Validation
    boolean existsByTitleAndProfile(String title, Long profileId);
}
