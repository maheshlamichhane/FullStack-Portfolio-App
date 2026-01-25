package com.portfolio.app.controller;

import com.portfolio.app.dto.*;
import com.portfolio.app.service.ExperienceService;
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
@RequestMapping("/api/v1/experiences")
@RequiredArgsConstructor
@Tag(name = "Experience", description = "Professional experience management APIs")
@Slf4j
public class ExperienceController {

    private final ExperienceService experienceService;

    @PostMapping
    @Operation(summary = "Create a new experience")
    public ResponseEntity<ExperienceResponseDTO> createExperience(
            @Valid @RequestBody ExperienceDTO experienceDTO) {
        log.info("POST /api/v1/experiences - Creating new experience");
        ExperienceResponseDTO createdExperience = experienceService.createExperience(experienceDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdExperience);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing experience")
    public ResponseEntity<ExperienceResponseDTO> updateExperience(
            @PathVariable Long id,
            @Valid @RequestBody ExperienceDTO experienceDTO) {
        log.info("PUT /api/v1/experiences/{} - Updating experience", id);
        ExperienceResponseDTO updatedExperience = experienceService.updateExperience(id, experienceDTO);
        return ResponseEntity.ok(updatedExperience);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get experience by ID")
    public ResponseEntity<ExperienceResponseDTO> getExperience(@PathVariable Long id) {
        log.debug("GET /api/v1/experiences/{} - Fetching experience", id);
        ExperienceResponseDTO experience = experienceService.getExperienceById(id);
        return ResponseEntity.ok(experience);
    }

    @GetMapping("/profile/{profileId}")
    @Operation(summary = "Get all experiences for a profile")
    public ResponseEntity<List<ExperienceResponseDTO>> getExperiencesByProfile(
            @PathVariable Long profileId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String technology,
            @RequestParam(defaultValue = "false") boolean currentOnly,
            @RequestParam(defaultValue = "false") boolean pastOnly) {
        log.debug("GET /api/v1/experiences/profile/{} - Fetching experiences", profileId);

        List<ExperienceResponseDTO> experiences;

        if (currentOnly) {
            experiences = experienceService.getCurrentExperiences(profileId);
        } else if (pastOnly) {
            experiences = experienceService.getPastExperiences(profileId);
        } else if (type != null && !type.trim().isEmpty()) {
            experiences = experienceService.getExperiencesByType(profileId, type);
        } else if (company != null && !company.trim().isEmpty()) {
            experiences = experienceService.searchExperiencesByCompany(profileId, company);
        } else if (technology != null && !technology.trim().isEmpty()) {
            experiences = experienceService.getExperiencesByTechnology(profileId, technology);
        } else {
            experiences = experienceService.getAllExperiencesByProfile(profileId);
        }

        return ResponseEntity.ok(experiences);
    }

    @GetMapping("/profile/{profileId}/paginated")
    @Operation(summary = "Get experiences for a profile with pagination")
    public ResponseEntity<Page<ExperienceResponseDTO>> getExperiencesByProfilePaginated(
            @PathVariable Long profileId,
            @PageableDefault(size = 10, sort = "startDate", direction = Sort.Direction.DESC)
            Pageable pageable) {
        log.debug("GET /api/v1/experiences/profile/{}/paginated - Fetching experiences with pagination", profileId);
        Page<ExperienceResponseDTO> experiences = experienceService.getExperiencesByProfile(profileId, pageable);
        return ResponseEntity.ok(experiences);
    }

    @GetMapping("/profile/{profileId}/recent")
    @Operation(summary = "Get recent experiences for a profile")
    public ResponseEntity<List<ExperienceSummaryDTO>> getRecentExperiences(
            @PathVariable Long profileId,
            @RequestParam(defaultValue = "5") int limit) {
        log.debug("GET /api/v1/experiences/profile/{}/recent?limit={} - Fetching recent experiences",
                profileId, limit);
        List<ExperienceSummaryDTO> experiences = experienceService.getRecentExperiences(profileId, limit);
        return ResponseEntity.ok(experiences);
    }

    @GetMapping("/profile/{profileId}/current")
    @Operation(summary = "Get current experiences for a profile")
    public ResponseEntity<List<ExperienceResponseDTO>> getCurrentExperiences(
            @PathVariable Long profileId) {
        log.debug("GET /api/v1/experiences/profile/{}/current - Fetching current experiences", profileId);
        List<ExperienceResponseDTO> experiences = experienceService.getCurrentExperiences(profileId);
        return ResponseEntity.ok(experiences);
    }

    @GetMapping("/profile/{profileId}/past")
    @Operation(summary = "Get past experiences for a profile")
    public ResponseEntity<List<ExperienceResponseDTO>> getPastExperiences(
            @PathVariable Long profileId) {
        log.debug("GET /api/v1/experiences/profile/{}/past - Fetching past experiences", profileId);
        List<ExperienceResponseDTO> experiences = experienceService.getPastExperiences(profileId);
        return ResponseEntity.ok(experiences);
    }

    @GetMapping("/stats/{profileId}")
    @Operation(summary = "Get experience statistics for a profile")
    public ResponseEntity<Map<String, Object>> getExperienceStats(@PathVariable Long profileId) {
        log.debug("GET /api/v1/experiences/stats/{} - Getting experience statistics", profileId);
        Map<String, Object> stats = experienceService.getExperienceStats(profileId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/total-years/{profileId}")
    @Operation(summary = "Calculate total experience in years")
    public ResponseEntity<Map<String, Object>> getTotalExperienceYears(@PathVariable Long profileId) {
        log.debug("GET /api/v1/experiences/total-years/{} - Calculating total experience years", profileId);
        Integer totalYears = experienceService.calculateTotalExperienceYears(profileId);

        Map<String, Object> response = new HashMap<>();
        response.put("profileId", profileId);
        response.put("totalYears", totalYears);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/employment-types/{profileId}")
    @Operation(summary = "Get employment type distribution")
    public ResponseEntity<Map<String, Long>> getEmploymentTypeDistribution(@PathVariable Long profileId) {
        log.debug("GET /api/v1/experiences/employment-types/{} - Getting employment type distribution", profileId);
        Map<String, Long> distribution = experienceService.getEmploymentTypeDistribution(profileId);
        return ResponseEntity.ok(distribution);
    }

    @GetMapping("/check-overlap/{profileId}")
    @Operation(summary = "Check if dates overlap with existing experiences")
    public ResponseEntity<Map<String, Boolean>> checkDateOverlap(
            @PathVariable Long profileId,
            @RequestParam String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "false") Boolean isCurrent,
            @RequestParam(required = false) Long excludeId) {
        log.debug("GET /api/v1/experiences/check-overlap/{} - Checking date overlap", profileId);

        boolean hasOverlap = experienceService.hasDateOverlap(
                profileId, excludeId, startDate, endDate, isCurrent);

        Map<String, Boolean> response = new HashMap<>();
        response.put("hasOverlap", hasOverlap);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an experience")
    public ResponseEntity<Void> deleteExperience(@PathVariable Long id) {
        log.info("DELETE /api/v1/experiences/{} - Deleting experience", id);
        experienceService.deleteExperience(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/profile/{profileId}")
    @Operation(summary = "Delete all experiences for a profile")
    public ResponseEntity<Void> deleteAllExperiencesByProfile(@PathVariable Long profileId) {
        log.info("DELETE /api/v1/experiences/profile/{} - Deleting all experiences", profileId);
        experienceService.deleteAllExperiencesByProfile(profileId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/timeline/{profileId}")
    @Operation(summary = "Get experience timeline for a profile")
    public ResponseEntity<List<Map<String, Object>>> getExperienceTimeline(@PathVariable Long profileId) {
        log.debug("GET /api/v1/experiences/timeline/{} - Getting experience timeline", profileId);

        Map<String, Object> stats = experienceService.getExperienceStats(profileId);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> timeline = (List<Map<String, Object>>) stats.get("timeline");

        return ResponseEntity.ok(timeline);
    }

    @GetMapping("/companies/{profileId}")
    @Operation(summary = "Get all companies worked at")
    public ResponseEntity<Map<String, Object>> getCompaniesWorkedAt(@PathVariable Long profileId) {
        log.debug("GET /api/v1/experiences/companies/{} - Getting companies worked at", profileId);

        List<ExperienceResponseDTO> experiences = experienceService.getAllExperiencesByProfile(profileId);
        List<String> companies = experiences.stream()
                .map(ExperienceResponseDTO::getCompany)
                .distinct()
                .sorted()
                .collect(java.util.stream.Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("profileId", profileId);
        response.put("companies", companies);
        response.put("count", companies.size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/technologies/{profileId}")
    @Operation(summary = "Get all technologies used in experiences")
    public ResponseEntity<Map<String, Object>> getTechnologiesUsed(@PathVariable Long profileId) {
        log.debug("GET /api/v1/experiences/technologies/{} - Getting technologies used", profileId);

        List<ExperienceResponseDTO> experiences = experienceService.getAllExperiencesByProfile(profileId);
        List<String> technologies = experiences.stream()
                .filter(e -> e.getTechnologies() != null)
                .flatMap(e -> e.getTechnologies().stream())
                .distinct()
                .sorted()
                .collect(java.util.stream.Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("profileId", profileId);
        response.put("technologies", technologies);
        response.put("count", technologies.size());

        return ResponseEntity.ok(response);
    }
}
