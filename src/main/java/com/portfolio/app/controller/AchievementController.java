package com.portfolio.app.controller;

import com.portfolio.app.dto.*;
import com.portfolio.app.service.AchievementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/achievements")
@RequiredArgsConstructor
@Tag(name = "Achievement", description = "Achievement and award management APIs")
@Slf4j
public class AchievementController {

    private final AchievementService achievementService;

    @PostMapping
    @Operation(summary = "Create a new achievement")
    public ResponseEntity<AchievementResponseDTO> createAchievement(
            @Valid @RequestBody AchievementDTO achievementDTO) {
        log.info("POST /api/v1/achievements - Creating new achievement: {}", achievementDTO.getTitle());
        AchievementResponseDTO createdAchievement = achievementService.createAchievement(achievementDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAchievement);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing achievement")
    public ResponseEntity<AchievementResponseDTO> updateAchievement(
            @PathVariable Long id,
            @Valid @RequestBody AchievementDTO achievementDTO) {
        log.info("PUT /api/v1/achievements/{} - Updating achievement", id);
        AchievementResponseDTO updatedAchievement = achievementService.updateAchievement(id, achievementDTO);
        return ResponseEntity.ok(updatedAchievement);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get achievement by ID")
    public ResponseEntity<AchievementResponseDTO> getAchievement(@PathVariable Long id) {
        log.debug("GET /api/v1/achievements/{} - Fetching achievement", id);
        AchievementResponseDTO achievement = achievementService.getAchievementById(id);
        return ResponseEntity.ok(achievement);
    }

    @GetMapping("/profile/{profileId}")
    @Operation(summary = "Get all achievements for a profile")
    public ResponseEntity<List<AchievementResponseDTO>> getAchievementsByProfile(
            @PathVariable Long profileId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean featured,
            @RequestParam(required = false) String organization,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String search) {
        log.debug("GET /api/v1/achievements/profile/{} - Fetching achievements", profileId);

        List<AchievementResponseDTO> achievements;

        if (search != null && !search.trim().isEmpty()) {
            achievements = achievementService.searchAchievements(profileId, search);
        } else if (category != null && !category.trim().isEmpty()) {
            achievements = achievementService.getAchievementsByCategory(profileId, category);
        } else if (featured != null && featured) {
            achievements = achievementService.getFeaturedAchievements(profileId);
        } else if (organization != null && !organization.trim().isEmpty()) {
            achievements = achievementService.getAchievementsByOrganization(profileId, organization);
        } else if (year != null) {
            achievements = achievementService.getAchievementsByYear(profileId, year);
        } else {
            achievements = achievementService.getAllAchievementsByProfile(profileId);
        }

        return ResponseEntity.ok(achievements);
    }

    @GetMapping("/profile/{profileId}/paginated")
    @Operation(summary = "Get achievements for a profile with pagination")
    public ResponseEntity<Page<AchievementResponseDTO>> getAchievementsByProfilePaginated(
            @PathVariable Long profileId,
            @PageableDefault(size = 20, sort = "dateReceived", direction = Sort.Direction.DESC)
            Pageable pageable) {
        log.debug("GET /api/v1/achievements/profile/{}/paginated - Fetching achievements with pagination", profileId);
        Page<AchievementResponseDTO> achievements = achievementService.getAchievementsByProfile(profileId, pageable);
        return ResponseEntity.ok(achievements);
    }

    @GetMapping("/profile/{profileId}/recent")
    @Operation(summary = "Get recent achievements")
    public ResponseEntity<List<AchievementResponseDTO>> getRecentAchievements(
            @PathVariable Long profileId,
            @RequestParam(defaultValue = "10") int limit) {
        log.debug("GET /api/v1/achievements/profile/{}/recent?limit={} - Getting recent achievements",
                profileId, limit);
        List<AchievementResponseDTO> achievements = achievementService.getRecentAchievements(profileId, limit);
        return ResponseEntity.ok(achievements);
    }

    @GetMapping("/profile/{profileId}/latest")
    @Operation(summary = "Get latest achievement")
    public ResponseEntity<AchievementResponseDTO> getLatestAchievement(@PathVariable Long profileId) {
        log.debug("GET /api/v1/achievements/profile/{}/latest - Getting latest achievement", profileId);
        AchievementResponseDTO achievement = achievementService.getLatestAchievement(profileId);
        return ResponseEntity.ok(achievement);
    }

    @GetMapping("/profile/{profileId}/grouped")
    @Operation(summary = "Get achievements grouped by category")
    public ResponseEntity<List<AchievementCategoryDTO>> getAchievementsGroupedByCategory(@PathVariable Long profileId) {
        log.debug("GET /api/v1/achievements/profile/{}/grouped - Getting achievements grouped by category", profileId);
        List<AchievementCategoryDTO> groupedAchievements = achievementService.getAchievementsGroupedByCategory(profileId);
        return ResponseEntity.ok(groupedAchievements);
    }

    @GetMapping("/profile/{profileId}/timeline")
    @Operation(summary = "Get achievements timeline")
    public ResponseEntity<List<AchievementTimelineDTO>> getAchievementsTimeline(@PathVariable Long profileId) {
        log.debug("GET /api/v1/achievements/profile/{}/timeline - Getting achievements timeline", profileId);
        List<AchievementTimelineDTO> timeline = achievementService.getAchievementsTimeline(profileId);
        return ResponseEntity.ok(timeline);
    }

    @GetMapping("/stats/{profileId}")
    @Operation(summary = "Get achievement statistics")
    public ResponseEntity<AchievementStatsDTO> getAchievementStats(@PathVariable Long profileId) {
        log.debug("GET /api/v1/achievements/stats/{} - Getting achievement statistics", profileId);
        AchievementStatsDTO stats = achievementService.getAchievementStats(profileId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/category-distribution/{profileId}")
    @Operation(summary = "Get category distribution")
    public ResponseEntity<Map<String, Long>> getCategoryDistribution(@PathVariable Long profileId) {
        log.debug("GET /api/v1/achievements/category-distribution/{} - Getting category distribution", profileId);
        Map<String, Long> distribution = achievementService.getCategoryDistribution(profileId);
        return ResponseEntity.ok(distribution);
    }

    @GetMapping("/yearly-distribution/{profileId}")
    @Operation(summary = "Get yearly distribution")
    public ResponseEntity<Map<Integer, Long>> getYearlyDistribution(@PathVariable Long profileId) {
        log.debug("GET /api/v1/achievements/yearly-distribution/{} - Getting yearly distribution", profileId);
        Map<Integer, Long> distribution = achievementService.getYearlyDistribution(profileId);
        return ResponseEntity.ok(distribution);
    }

    @GetMapping("/categories/{profileId}")
    @Operation(summary = "Get distinct categories")
    public ResponseEntity<List<String>> getDistinctCategories(@PathVariable Long profileId) {
        log.debug("GET /api/v1/achievements/categories/{} - Getting distinct categories", profileId);
        List<String> categories = achievementService.getDistinctCategories(profileId);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/organizations/{profileId}")
    @Operation(summary = "Get distinct organizations")
    public ResponseEntity<List<String>> getDistinctOrganizations(@PathVariable Long profileId) {
        log.debug("GET /api/v1/achievements/organizations/{} - Getting distinct organizations", profileId);
        List<String> organizations = achievementService.getDistinctOrganizations(profileId);
        return ResponseEntity.ok(organizations);
    }

    @GetMapping("/total-years/{profileId}")
    @Operation(summary = "Get total achievement years")
    public ResponseEntity<Map<String, Object>> getTotalAchievementYears(@PathVariable Long profileId) {
        log.debug("GET /api/v1/achievements/total-years/{} - Getting total achievement years", profileId);
        Integer totalYears = achievementService.getTotalAchievementYears(profileId);

        Map<String, Object> response = new HashMap<>();
        response.put("profileId", profileId);
        response.put("totalYears", totalYears);
        response.put("message", String.format("Achievements span across %d year(s)", totalYears));

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/toggle-featured")
    @Operation(summary = "Toggle featured status of an achievement")
    public ResponseEntity<AchievementResponseDTO> toggleFeatured(@PathVariable Long id) {
        log.info("PATCH /api/v1/achievements/{}/toggle-featured - Toggling featured status", id);
        AchievementResponseDTO achievement = achievementService.toggleFeatured(id);
        return ResponseEntity.ok(achievement);
    }

    @GetMapping("/check-title")
    @Operation(summary = "Check if achievement title exists for profile")
    public ResponseEntity<Map<String, Boolean>> checkAchievementTitle(
            @RequestParam String title,
            @RequestParam Long profileId) {
        log.debug("GET /api/v1/achievements/check-title?title={}&profileId={} - Checking achievement title",
                title, profileId);
        boolean exists = achievementService.existsByTitleAndProfile(title, profileId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an achievement")
    public ResponseEntity<Void> deleteAchievement(@PathVariable Long id) {
        log.info("DELETE /api/v1/achievements/{} - Deleting achievement", id);
        achievementService.deleteAchievement(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/profile/{profileId}")
    @Operation(summary = "Delete all achievements for a profile")
    public ResponseEntity<Void> deleteAllAchievementsByProfile(@PathVariable Long profileId) {
        log.info("DELETE /api/v1/achievements/profile/{} - Deleting all achievements", profileId);
        achievementService.deleteAllAchievementsByProfile(profileId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/profile/{profileId}/category/{category}")
    @Operation(summary = "Delete achievements by category")
    public ResponseEntity<Map<String, Object>> deleteAchievementsByCategory(
            @PathVariable Long profileId,
            @PathVariable String category) {
        log.info("DELETE /api/v1/achievements/profile/{}/category/{} - Deleting achievements by category",
                profileId, category);
        achievementService.deleteAchievementsByCategory(profileId, category);

        Map<String, Object> response = new HashMap<>();
        response.put("message", String.format("Achievements in category '%s' deleted successfully", category));
        response.put("profileId", profileId);
        response.put("category", category);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/export/{profileId}")
    @Operation(summary = "Export achievements")
    public ResponseEntity<Map<String, Object>> exportAchievements(
            @PathVariable Long profileId,
            @RequestParam(defaultValue = "json") String format) {
        log.debug("GET /api/v1/achievements/export/{}?format={} - Exporting achievements", profileId, format);

        List<AchievementResponseDTO> achievements = achievementService.getAllAchievementsByProfile(profileId);
        AchievementStatsDTO stats = achievementService.getAchievementStats(profileId);
        Map<String, Long> categoryDistribution = achievementService.getCategoryDistribution(profileId);

        Map<String, Object> exportData = new HashMap<>();
        exportData.put("generatedAt", java.time.LocalDateTime.now().toString());
        exportData.put("profileId", profileId);
        exportData.put("totalAchievements", achievements.size());
        exportData.put("achievements", achievements);
        exportData.put("statistics", stats);
        exportData.put("categoryDistribution", categoryDistribution);
        exportData.put("format", format);

        return ResponseEntity.ok(exportData);
    }

    @GetMapping("/timeline-chart/{profileId}")
    @Operation(summary = "Get achievements timeline chart data")
    public ResponseEntity<Map<String, Object>> getAchievementsTimelineChart(@PathVariable Long profileId) {
        log.debug("GET /api/v1/achievements/timeline-chart/{} - Getting achievements timeline chart data", profileId);

        List<AchievementTimelineDTO> timeline = achievementService.getAchievementsTimeline(profileId);
        Map<Integer, Long> yearlyDistribution = achievementService.getYearlyDistribution(profileId);
        Map<String, Long> categoryDistribution = achievementService.getCategoryDistribution(profileId);

        Map<String, Object> chartData = new HashMap<>();

        // Prepare data for timeline chart
        Map<Integer, Map<String, Long>> yearlyCategoryData = new TreeMap<>(Collections.reverseOrder());

        for (AchievementTimelineDTO achievement : timeline) {
            int year = achievement.getYear();
            String category = achievement.getCategory();

            yearlyCategoryData
                    .computeIfAbsent(year, k -> new HashMap<>())
                    .merge(category, 1L, Long::sum);
        }

        // Prepare category counts for pie chart
        List<Map<String, Object>> categoryData = new ArrayList<>();
        for (Map.Entry<String, Long> entry : categoryDistribution.entrySet()) {
            Map<String, Object> categoryItem = new HashMap<>();
            categoryItem.put("category", entry.getKey());
            categoryItem.put("count", entry.getValue());
            categoryData.add(categoryItem);
        }

        chartData.put("timeline", timeline);
        chartData.put("yearlyDistribution", yearlyDistribution);
        chartData.put("yearlyCategoryData", yearlyCategoryData);
        chartData.put("categoryData", categoryData);

        return ResponseEntity.ok(chartData);
    }

    @GetMapping("/milestones/{profileId}")
    @Operation(summary = "Get achievement milestones")
    public ResponseEntity<Map<String, Object>> getAchievementMilestones(@PathVariable Long profileId) {
        log.debug("GET /api/v1/achievements/milestones/{} - Getting achievement milestones", profileId);

        List<AchievementResponseDTO> achievements = achievementService.getAllAchievementsByProfile(profileId);
        AchievementStatsDTO stats = achievementService.getAchievementStats(profileId);

        Map<String, Object> milestones = new HashMap<>();

        if (!achievements.isEmpty()) {
            // First achievement
            AchievementResponseDTO firstAchievement = achievements.get(achievements.size() - 1);

            // Most recent achievement
            AchievementResponseDTO latestAchievement = achievements.get(0);

            // Achievement with most years ago
            AchievementResponseDTO oldestAchievement = achievements.stream()
                    .max(Comparator.comparingInt(AchievementResponseDTO::getYearsAgo))
                    .orElse(null);

            // Count achievements with links
            long achievementsWithLinks = achievements.stream()
                    .filter(a -> a.getLink() != null && !a.getLink().trim().isEmpty())
                    .count();

            // Count achievements with icons
            long achievementsWithIcons = achievements.stream()
                    .filter(a -> a.getIconUrl() != null && !a.getIconUrl().trim().isEmpty())
                    .count();

            milestones.put("firstAchievement", firstAchievement);
            milestones.put("latestAchievement", latestAchievement);
            milestones.put("oldestAchievement", oldestAchievement);
            milestones.put("achievementsWithLinks", achievementsWithLinks);
            milestones.put("achievementsWithIcons", achievementsWithIcons);
            milestones.put("featuredAchievementsCount", stats.getFeaturedAchievements());
            milestones.put("totalCategories", stats.getCategoryDistribution().size());
        }

        milestones.put("profileId", profileId);
        milestones.put("totalAchievements", achievements.size());

        return ResponseEntity.ok(milestones);
    }
}
