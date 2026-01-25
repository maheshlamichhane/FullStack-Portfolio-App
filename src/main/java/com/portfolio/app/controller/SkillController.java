package com.portfolio.app.controller;

import com.portfolio.app.dto.*;
import com.portfolio.app.service.SkillService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/skills")
@RequiredArgsConstructor
@Tag(name = "Skill", description = "Skill management APIs")
@Slf4j
public class SkillController {

    private final SkillService skillService;

    @PostMapping
    @Operation(summary = "Create a new skill")
    public ResponseEntity<SkillResponseDTO> createSkill(
            @Valid @RequestBody SkillDTO skillDTO) {
        log.info("POST /api/v1/skills - Creating new skill: {}", skillDTO.getName());
        SkillResponseDTO createdSkill = skillService.createSkill(skillDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSkill);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing skill")
    public ResponseEntity<SkillResponseDTO> updateSkill(
            @PathVariable Long id,
            @Valid @RequestBody SkillDTO skillDTO) {
        log.info("PUT /api/v1/skills/{} - Updating skill", id);
        SkillResponseDTO updatedSkill = skillService.updateSkill(id, skillDTO);
        return ResponseEntity.ok(updatedSkill);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get skill by ID")
    public ResponseEntity<SkillResponseDTO> getSkill(@PathVariable Long id) {
        log.debug("GET /api/v1/skills/{} - Fetching skill", id);
        SkillResponseDTO skill = skillService.getSkillById(id);
        return ResponseEntity.ok(skill);
    }

    @GetMapping("/profile/{profileId}")
    @Operation(summary = "Get all skills for a profile")
    public ResponseEntity<List<SkillResponseDTO>> getSkillsByProfile(
            @PathVariable Long profileId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean featured,
            @RequestParam(required = false) Integer minProficiency,
            @RequestParam(required = false) Integer maxProficiency,
            @RequestParam(required = false) String search) {
        log.debug("GET /api/v1/skills/profile/{} - Fetching skills", profileId);

        List<SkillResponseDTO> skills;

        if (search != null && !search.trim().isEmpty()) {
            skills = skillService.searchSkills(profileId, search);
        } else if (category != null && !category.trim().isEmpty()) {
            skills = skillService.getSkillsByCategory(profileId, category);
        } else if (featured != null && featured) {
            skills = skillService.getFeaturedSkills(profileId);
        } else if (minProficiency != null || maxProficiency != null) {
            skills = skillService.getSkillsByProficiencyRange(profileId, minProficiency, maxProficiency);
        } else {
            skills = skillService.getAllSkillsByProfile(profileId);
        }

        return ResponseEntity.ok(skills);
    }

    @GetMapping("/profile/{profileId}/paginated")
    @Operation(summary = "Get skills for a profile with pagination")
    public ResponseEntity<Page<SkillResponseDTO>> getSkillsByProfilePaginated(
            @PathVariable Long profileId,
            @PageableDefault(size = 20, sort = "displayOrder", direction = Sort.Direction.ASC)
            Pageable pageable) {
        log.debug("GET /api/v1/skills/profile/{}/paginated - Fetching skills with pagination", profileId);
        Page<SkillResponseDTO> skills = skillService.getSkillsByProfile(profileId, pageable);
        return ResponseEntity.ok(skills);
    }

    @GetMapping("/profile/{profileId}/display-order")
    @Operation(summary = "Get skills ordered by display order")
    public ResponseEntity<List<SkillSummaryDTO>> getSkillsByDisplayOrder(@PathVariable Long profileId) {
        log.debug("GET /api/v1/skills/profile/{}/display-order - Fetching skills by display order", profileId);
        List<SkillSummaryDTO> skills = skillService.getSkillsByDisplayOrder(profileId);
        return ResponseEntity.ok(skills);
    }

    @GetMapping("/profile/{profileId}/grouped")
    @Operation(summary = "Get skills grouped by category")
    public ResponseEntity<List<SkillCategoryDTO>> getSkillsGroupedByCategory(@PathVariable Long profileId) {
        log.debug("GET /api/v1/skills/profile/{}/grouped - Getting skills grouped by category", profileId);
        List<SkillCategoryDTO> groupedSkills = skillService.getSkillsGroupedByCategory(profileId);
        return ResponseEntity.ok(groupedSkills);
    }

    @GetMapping("/profile/{profileId}/top")
    @Operation(summary = "Get top skills for a profile")
    public ResponseEntity<List<SkillResponseDTO>> getTopSkills(
            @PathVariable Long profileId,
            @RequestParam(defaultValue = "10") int limit) {
        log.debug("GET /api/v1/skills/profile/{}/top?limit={} - Getting top skills", profileId, limit);
        List<SkillResponseDTO> topSkills = skillService.getTopSkills(profileId, limit);
        return ResponseEntity.ok(topSkills);
    }

    @GetMapping("/stats/{profileId}")
    @Operation(summary = "Get skill statistics for a profile")
    public ResponseEntity<SkillStatsDTO> getSkillStats(@PathVariable Long profileId) {
        log.debug("GET /api/v1/skills/stats/{} - Getting skill statistics", profileId);
        SkillStatsDTO stats = skillService.getSkillStats(profileId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/proficiency-distribution/{profileId}")
    @Operation(summary = "Get proficiency level distribution")
    public ResponseEntity<Map<String, Integer>> getProficiencyDistribution(@PathVariable Long profileId) {
        log.debug("GET /api/v1/skills/proficiency-distribution/{} - Getting proficiency distribution", profileId);
        Map<String, Integer> distribution = skillService.getProficiencyDistribution(profileId);
        return ResponseEntity.ok(distribution);
    }

    @GetMapping("/categories/{profileId}")
    @Operation(summary = "Get all skill categories for a profile")
    public ResponseEntity<List<String>> getSkillCategories(@PathVariable Long profileId) {
        log.debug("GET /api/v1/skills/categories/{} - Getting skill categories", profileId);
        List<String> categories = skillService.getSkillCategories(profileId);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/total-experience/{profileId}")
    @Operation(summary = "Get total years of experience")
    public ResponseEntity<Map<String, Object>> getTotalYearsOfExperience(@PathVariable Long profileId) {
        log.debug("GET /api/v1/skills/total-experience/{} - Getting total years of experience", profileId);
        Integer totalYears = skillService.getTotalYearsOfExperience(profileId);

        Map<String, Object> response = new HashMap<>();
        response.put("profileId", profileId);
        response.put("totalYears", totalYears);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/toggle-featured")
    @Operation(summary = "Toggle featured status of a skill")
    public ResponseEntity<SkillResponseDTO> toggleFeatured(@PathVariable Long id) {
        log.info("PATCH /api/v1/skills/{}/toggle-featured - Toggling featured status", id);
        SkillResponseDTO skill = skillService.toggleFeatured(id);
        return ResponseEntity.ok(skill);
    }

    @PatchMapping("/{id}/display-order")
    @Operation(summary = "Update display order of a skill")
    public ResponseEntity<SkillResponseDTO> updateDisplayOrder(
            @PathVariable Long id,
            @RequestParam Integer displayOrder) {
        log.info("PATCH /api/v1/skills/{}/display-order?displayOrder={} - Updating display order",
                id, displayOrder);
        SkillResponseDTO skill = skillService.updateDisplayOrder(id, displayOrder);
        return ResponseEntity.ok(skill);
    }

    @PatchMapping("/batch/display-orders")
    @Operation(summary = "Update multiple display orders")
    public ResponseEntity<Void> updateMultipleDisplayOrders(
            @RequestBody Map<Long, Integer> displayOrderUpdates) {
        log.info("PATCH /api/v1/skills/batch/display-orders - Updating multiple display orders");
        skillService.updateMultipleDisplayOrders(displayOrderUpdates);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check-name")
    @Operation(summary = "Check if skill name is available")
    public ResponseEntity<Map<String, Boolean>> checkSkillName(
            @RequestParam String name,
            @RequestParam Long profileId) {
        log.debug("GET /api/v1/skills/check-name?name={}&profileId={} - Checking skill name availability",
                name, profileId);
        boolean exists = skillService.existsByNameAndProfile(name, profileId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        response.put("available", !exists);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-display-order")
    @Operation(summary = "Check if display order is available")
    public ResponseEntity<Map<String, Boolean>> checkDisplayOrder(
            @RequestParam Long profileId,
            @RequestParam Integer displayOrder,
            @RequestParam(required = false) Long excludeId) {
        log.debug("GET /api/v1/skills/check-display-order - Checking display order availability");
        boolean available = skillService.isDisplayOrderAvailable(profileId, displayOrder, excludeId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("available", available);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a skill")
    public ResponseEntity<Void> deleteSkill(@PathVariable Long id) {
        log.info("DELETE /api/v1/skills/{} - Deleting skill", id);
        skillService.deleteSkill(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/profile/{profileId}")
    @Operation(summary = "Delete all skills for a profile")
    public ResponseEntity<Void> deleteAllSkillsByProfile(@PathVariable Long profileId) {
        log.info("DELETE /api/v1/skills/profile/{} - Deleting all skills", profileId);
        skillService.deleteAllSkillsByProfile(profileId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/export/{profileId}")
    @Operation(summary = "Export skills as CSV/JSON")
    public ResponseEntity<List<SkillResponseDTO>> exportSkills(
            @PathVariable Long profileId,
            @RequestParam(defaultValue = "json") String format) {
        log.debug("GET /api/v1/skills/export/{}?format={} - Exporting skills", profileId, format);

        List<SkillResponseDTO> skills = skillService.getAllSkillsByProfile(profileId);

        // In a real application, you might want to implement different export formats
        // For now, we just return JSON
        return ResponseEntity.ok(skills);
    }

    @GetMapping("/heatmap/{profileId}")
    @Operation(summary = "Get skill heatmap data")
    public ResponseEntity<Map<String, Object>> getSkillHeatmap(@PathVariable Long profileId) {
        log.debug("GET /api/v1/skills/heatmap/{} - Getting skill heatmap data", profileId);

        List<SkillCategoryDTO> groupedSkills = skillService.getSkillsGroupedByCategory(profileId);
        SkillStatsDTO stats = skillService.getSkillStats(profileId);

        Map<String, Object> heatmapData = new HashMap<>();
        heatmapData.put("groupedSkills", groupedSkills);
        heatmapData.put("stats", stats);

        return ResponseEntity.ok(heatmapData);
    }
}
