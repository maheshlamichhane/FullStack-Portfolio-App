package com.portfolio.app.controller;

import com.portfolio.app.dto.*;
import com.portfolio.app.service.ProjectService;
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
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Tag(name = "Project", description = "Project management APIs")
@Slf4j
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @Operation(summary = "Create a new project")
    public ResponseEntity<ProjectResponseDTO> createProject(
            @Valid @RequestBody ProjectDTO projectDTO) {
        log.info("POST /api/v1/projects - Creating new project: {}", projectDTO.getTitle());
        ProjectResponseDTO createdProject = projectService.createProject(projectDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing project")
    public ResponseEntity<ProjectResponseDTO> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectDTO projectDTO) {
        log.info("PUT /api/v1/projects/{} - Updating project", id);
        ProjectResponseDTO updatedProject = projectService.updateProject(id, projectDTO);
        return ResponseEntity.ok(updatedProject);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get project by ID")
    public ResponseEntity<ProjectResponseDTO> getProject(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean publishedOnly) {
        log.debug("GET /api/v1/projects/{} - Fetching project", id);

        ProjectResponseDTO project;
        if (publishedOnly) {
            project = projectService.getPublishedProjectById(id);
        } else {
            project = projectService.getProjectById(id);
        }

        return ResponseEntity.ok(project);
    }

    @GetMapping("/profile/{profileId}")
    @Operation(summary = "Get all projects for a profile")
    public ResponseEntity<List<ProjectResponseDTO>> getProjectsByProfile(
            @PathVariable Long profileId,
            @RequestParam(required = false) Boolean featured,
            @RequestParam(required = false) Boolean published,
            @RequestParam(required = false) String technology,
            @RequestParam(required = false) String search) {
        log.debug("GET /api/v1/projects/profile/{} - Fetching projects", profileId);

        List<ProjectResponseDTO> projects;

        if (search != null && !search.trim().isEmpty()) {
            // For search, we use paginated version, but here we return all. We can adjust if needed.
            // Let's use the search method with default pagination and return the content.
            Pageable pageable = Pageable.unpaged();
            Page<ProjectResponseDTO> page = projectService.searchProjects(profileId, search, pageable);
            projects = page.getContent();
        } else if (technology != null && !technology.trim().isEmpty()) {
            Pageable pageable = Pageable.unpaged();
            Page<ProjectResponseDTO> page = projectService.getProjectsByTechnology(profileId, technology, pageable);
            projects = page.getContent();
        } else if (featured != null && featured) {
            if (published != null && published) {
                projects = projectService.getPublishedFeaturedProjects(profileId);
            } else {
                projects = projectService.getFeaturedProjects(profileId);
            }
        } else if (published != null && published) {
            Pageable pageable = Pageable.unpaged();
            Page<ProjectResponseDTO> page = projectService.getPublishedProjectsByProfile(profileId, pageable);
            projects = page.getContent();
        } else {
            projects = projectService.getAllProjectsByProfile(profileId);
        }

        return ResponseEntity.ok(projects);
    }

    @GetMapping("/profile/{profileId}/paginated")
    @Operation(summary = "Get projects for a profile with pagination")
    public ResponseEntity<Page<ProjectResponseDTO>> getProjectsByProfilePaginated(
            @PathVariable Long profileId,
            @PageableDefault(size = 10, sort = "startDate", direction = Sort.Direction.DESC)
            Pageable pageable,
            @RequestParam(required = false) Boolean published,
            @RequestParam(required = false) String technology,
            @RequestParam(required = false) String search) {
        log.debug("GET /api/v1/projects/profile/{}/paginated - Fetching projects with pagination", profileId);

        Page<ProjectResponseDTO> projects;

        if (search != null && !search.trim().isEmpty()) {
            projects = projectService.searchProjects(profileId, search, pageable);
        } else if (technology != null && !technology.trim().isEmpty()) {
            projects = projectService.getProjectsByTechnology(profileId, technology, pageable);
        } else if (published != null && published) {
            projects = projectService.getPublishedProjectsByProfile(profileId, pageable);
        } else {
            projects = projectService.getProjectsByProfile(profileId, pageable);
        }

        return ResponseEntity.ok(projects);
    }

    @GetMapping("/profile/{profileId}/recent")
    @Operation(summary = "Get recent projects for a profile")
    public ResponseEntity<List<ProjectSummaryDTO>> getRecentProjects(
            @PathVariable Long profileId,
            @RequestParam(defaultValue = "5") int limit) {
        log.debug("GET /api/v1/projects/profile/{}/recent?limit={} - Fetching recent projects",
                profileId, limit);
        List<ProjectSummaryDTO> projects = projectService.getRecentProjects(profileId, limit);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/stats/{profileId}")
    @Operation(summary = "Get project statistics for a profile")
    public ResponseEntity<ProjectStatsDTO> getProjectStats(@PathVariable Long profileId) {
        log.debug("GET /api/v1/projects/stats/{} - Getting project statistics", profileId);
        ProjectStatsDTO stats = projectService.getProjectStats(profileId);
        return ResponseEntity.ok(stats);
    }

    @PatchMapping("/{id}/toggle-featured")
    @Operation(summary = "Toggle featured status of a project")
    public ResponseEntity<ProjectResponseDTO> toggleFeatured(@PathVariable Long id) {
        log.info("PATCH /api/v1/projects/{}/toggle-featured - Toggling featured status", id);
        ProjectResponseDTO project = projectService.toggleFeatured(id);
        return ResponseEntity.ok(project);
    }

    @PatchMapping("/{id}/toggle-published")
    @Operation(summary = "Toggle published status of a project")
    public ResponseEntity<ProjectResponseDTO> togglePublished(@PathVariable Long id) {
        log.info("PATCH /api/v1/projects/{}/toggle-published - Toggling published status", id);
        ProjectResponseDTO project = projectService.togglePublished(id);
        return ResponseEntity.ok(project);
    }

    @PostMapping("/{projectId}/images")
    @Operation(summary = "Add an image to a project")
    public ResponseEntity<ProjectResponseDTO> addImageToProject(
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectImageDTO imageDTO) {
        log.info("POST /api/v1/projects/{}/images - Adding image to project", projectId);
        ProjectResponseDTO project = projectService.addImageToProject(projectId, imageDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(project);
    }

    @DeleteMapping("/{projectId}/images/{imageId}")
    @Operation(summary = "Remove an image from a project")
    public ResponseEntity<ProjectResponseDTO> removeImageFromProject(
            @PathVariable Long projectId,
            @PathVariable Long imageId) {
        log.info("DELETE /api/v1/projects/{}/images/{} - Removing image from project", projectId, imageId);
        ProjectResponseDTO project = projectService.removeImageFromProject(projectId, imageId);
        return ResponseEntity.ok(project);
    }

    @GetMapping("/check-title")
    @Operation(summary = "Check if project title is available")
    public ResponseEntity<Map<String, Boolean>> checkProjectTitle(
            @RequestParam String title,
            @RequestParam Long profileId) {
        log.debug("GET /api/v1/projects/check-title?title={}&profileId={} - Checking project title availability",
                title, profileId);
        boolean exists = projectService.existsByTitleAndProfile(title, profileId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        response.put("available", !exists);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a project")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        log.info("DELETE /api/v1/projects/{} - Deleting project", id);
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/profile/{profileId}")
    @Operation(summary = "Delete all projects for a profile")
    public ResponseEntity<Void> deleteAllProjectsByProfile(@PathVariable Long profileId) {
        log.info("DELETE /api/v1/projects/profile/{} - Deleting all projects", profileId);
        projectService.deleteAllProjectsByProfile(profileId);
        return ResponseEntity.noContent().build();
    }
}
