package com.portfolio.app.service;

import com.portfolio.app.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ProjectService {

    // Create
    ProjectResponseDTO createProject(ProjectDTO projectDTO);

    // Read
    ProjectResponseDTO getProjectById(Long id);
    ProjectResponseDTO getPublishedProjectById(Long id);
    List<ProjectResponseDTO> getAllProjectsByProfile(Long profileId);
    Page<ProjectResponseDTO> getProjectsByProfile(Long profileId, Pageable pageable);
    Page<ProjectResponseDTO> getPublishedProjectsByProfile(Long profileId, Pageable pageable);
    List<ProjectResponseDTO> getFeaturedProjects(Long profileId);
    List<ProjectResponseDTO> getPublishedFeaturedProjects(Long profileId);
    Page<ProjectResponseDTO> getProjectsByTechnology(Long profileId, String technology, Pageable pageable);
    Page<ProjectResponseDTO> searchProjects(Long profileId, String query, Pageable pageable);
    List<ProjectSummaryDTO> getRecentProjects(Long profileId, int limit);

    // Update
    ProjectResponseDTO updateProject(Long id, ProjectDTO projectDTO);
    ProjectResponseDTO toggleFeatured(Long id);
    ProjectResponseDTO togglePublished(Long id);
    ProjectResponseDTO addImageToProject(Long projectId, ProjectImageDTO imageDTO);
    ProjectResponseDTO removeImageFromProject(Long projectId, Long imageId);

    // Delete
    void deleteProject(Long id);
    void deleteAllProjectsByProfile(Long profileId);

    // Statistics
    ProjectStatsDTO getProjectStats(Long profileId);

    // Validation
    boolean existsByTitleAndProfile(String title, Long profileId);
}
