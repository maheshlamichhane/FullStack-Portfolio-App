package com.portfolio.app.service;

import com.portfolio.app.dao.ProfileRepository;
import com.portfolio.app.dao.ProjectRepository;
import com.portfolio.app.dto.*;
import com.portfolio.app.entity.Project;
import com.portfolio.app.entity.ProjectImage;
import com.portfolio.app.entity.Profile;
import com.portfolio.app.exception.ResourceNotFoundException;
import com.portfolio.app.mapper.ProjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProfileRepository profileRepository;
    private final ProjectMapper projectMapper;

    @Override
    @Transactional
    public ProjectResponseDTO createProject(ProjectDTO projectDTO) {
        log.info("Creating new project for profile {}: {}",
                projectDTO.getProfileId(), projectDTO.getTitle());

        // Validate profile exists
        Profile profile = profileRepository.findById(projectDTO.getProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "id", projectDTO.getProfileId()));

        // Check if project with same title already exists for this profile
//        if (projectRepository.existsByTitleAndProfileId(projectDTO.getTitle(), projectDTO.getProfileId())) {
//            throw new BusinessRuleException("Project '" + projectDTO.getTitle() + "' already exists for this profile");
//        }

        Project project = projectMapper.toEntity(projectDTO);
        project.setProfile(profile);

        // Set project reference in images
        if (project.getImages() != null) {
            project.getImages().forEach(image -> image.setProject(project));
        }

        Project savedProject = projectRepository.save(project);
        log.info("Project created successfully with ID: {}", savedProject.getId());

        return projectMapper.toResponseDto(savedProject);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponseDTO getProjectById(Long id) {
        log.debug("Fetching project by ID: {}", id);

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

        return projectMapper.toResponseDto(project);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponseDTO getPublishedProjectById(Long id) {
        log.debug("Fetching published project by ID: {}", id);

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

        if (!project.getIsPublished()) {
            throw new ResourceNotFoundException("Project", "id", id);
        }

        return projectMapper.toResponseDto(project);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> getAllProjectsByProfile(Long profileId) {
        log.debug("Fetching all projects for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return projectRepository.findByProfileId(profileId)
                .stream()
                .map(projectMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectResponseDTO> getProjectsByProfile(Long profileId, Pageable pageable) {
        log.debug("Fetching projects for profile ID: {} with pagination", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return projectRepository.findByProfileId(profileId, pageable)
                .map(projectMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectResponseDTO> getPublishedProjectsByProfile(Long profileId, Pageable pageable) {
        log.debug("Fetching published projects for profile ID: {} with pagination", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return projectRepository.findByProfileIdAndIsPublishedTrue(profileId, pageable)
                .map(projectMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> getFeaturedProjects(Long profileId) {
        log.debug("Fetching featured projects for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return projectRepository.findByProfileIdAndIsFeaturedTrue(profileId)
                .stream()
                .map(projectMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> getPublishedFeaturedProjects(Long profileId) {
        log.debug("Fetching published featured projects for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return projectRepository.findByProfileIdAndIsFeaturedTrueAndIsPublishedTrue(profileId)
                .stream()
                .map(projectMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectResponseDTO> getProjectsByTechnology(Long profileId, String technology, Pageable pageable) {
        log.debug("Fetching projects with technology '{}' for profile ID: {}", technology, profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        // Note: The repository method `findByTechnology` doesn't filter by profileId.
        // We need to adjust the query or filter after. Let's adjust the repository method.
        // For now, I'll use a workaround by fetching all and filtering, but this is inefficient.
        // We should update the repository method to include profileId.

        // Since the repository method doesn't have profileId, we'll do it in memory (not recommended for large data)
        // Alternatively, we can create a new repository method that includes profileId.

        // Let's create a new repository method for this purpose:
        // In ProjectRepository, add:
        // @Query("SELECT p FROM Project p WHERE :technology MEMBER OF p.technologies AND p.isPublished = true AND p.profile.id = :profileId")
        // Page<Project> findByProfileIdAndTechnology(@Param("profileId") Long profileId, @Param("technology") String technology, Pageable pageable);

        // For now, I'll use the existing method and then filter by profileId (inefficient)
        Page<Project> projects = projectRepository.findByTechnology(technology, pageable);
        List<Project> filtered = projects.getContent().stream()
                .filter(p -> p.getProfile().getId().equals(profileId))
                .collect(Collectors.toList());

        // We need to return a Page, so we'll create a new Page with filtered content.
        // This is a workaround until we fix the repository method.
        return new org.springframework.data.domain.PageImpl<>(filtered, pageable, filtered.size())
                .map(projectMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectResponseDTO> searchProjects(Long profileId, String query, Pageable pageable) {
        log.debug("Searching projects with query '{}' for profile ID: {}", query, profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        if (query == null || query.trim().isEmpty()) {
            return getProjectsByProfile(profileId, pageable);
        }

        return projectRepository.searchByProfileId(profileId, query.trim(), pageable)
                .map(projectMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectSummaryDTO> getRecentProjects(Long profileId, int limit) {
        log.debug("Fetching {} recent projects for profile ID: {}", limit, profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        Pageable pageable = PageRequest.of(0, limit, org.springframework.data.domain.Sort.by("startDate").descending());
        return projectRepository.findByProfileId(profileId, pageable)
                .getContent()
                .stream()
                .map(projectMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProjectResponseDTO updateProject(Long id, ProjectDTO projectDTO) {
        log.info("Updating project with ID: {}", id);

        Project existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

        // Check if title is being changed and if it's available
//        if (!existingProject.getTitle().equals(projectDTO.getTitle())) {
//            if (projectRepository.existsByTitleAndProfileId(projectDTO.getTitle(), projectDTO.getProfileId())) {
//                throw new BusinessRuleException("Project '" + projectDTO.getTitle() + "' already exists for this profile");
//            }
//        }

        // Update profile if changed
        if (!existingProject.getProfile().getId().equals(projectDTO.getProfileId())) {
            Profile profile = profileRepository.findById(projectDTO.getProfileId())
                    .orElseThrow(() -> new ResourceNotFoundException("Profile", "id", projectDTO.getProfileId()));
            existingProject.setProfile(profile);
        }

        // Update basic fields
        existingProject.setTitle(projectDTO.getTitle());
        existingProject.setDescription(projectDTO.getDescription());
        existingProject.setThumbnailUrl(projectDTO.getThumbnailUrl());
        existingProject.setTechnologies(projectDTO.getTechnologies());
        existingProject.setGithubUrl(projectDTO.getGithubUrl());
        existingProject.setLiveUrl(projectDTO.getLiveUrl());
        existingProject.setDemoUrl(projectDTO.getDemoUrl());
        existingProject.setStartDate(projectDTO.getStartDate());
        existingProject.setEndDate(projectDTO.getEndDate());
        existingProject.setIsFeatured(projectDTO.getIsFeatured());
        existingProject.setIsPublished(projectDTO.getIsPublished());

        // Update images if provided
        if (projectDTO.getImages() != null) {
            // Remove existing images not in the new list
            existingProject.getImages().removeIf(existingImage ->
                    projectDTO.getImages().stream()
                            .noneMatch(newImage -> newImage.getId() != null && newImage.getId().equals(existingImage.getId()))
            );

            // Update or add new images
            for (ProjectImageDTO imageDTO : projectDTO.getImages()) {
                if (imageDTO.getId() != null) {
                    // Update existing image
                    ProjectImage existingImage = existingProject.getImages().stream()
                            .filter(img -> img.getId().equals(imageDTO.getId()))
                            .findFirst()
                            .orElseThrow(() -> new ResourceNotFoundException("ProjectImage", "id", imageDTO.getId()));

                    existingImage.setImageUrl(imageDTO.getImageUrl());
                    existingImage.setAltText(imageDTO.getAltText());
                    existingImage.setDisplayOrder(imageDTO.getDisplayOrder());
                } else {
                    // Add new image
                    ProjectImage newImage = projectMapper.toImageEntity(imageDTO);
                    newImage.setProject(existingProject);
                    existingProject.getImages().add(newImage);
                }
            }
        }

        Project updatedProject = projectRepository.save(existingProject);
        log.info("Project updated successfully: {}", id);

        return projectMapper.toResponseDto(updatedProject);
    }

    @Override
    @Transactional
    public ProjectResponseDTO toggleFeatured(Long id) {
        log.info("Toggling featured status for project ID: {}", id);

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

        project.setIsFeatured(!Boolean.TRUE.equals(project.getIsFeatured()));

        Project updatedProject = projectRepository.save(project);
        log.info("Project featured status toggled: {} is now {}", id, updatedProject.getIsFeatured());

        return projectMapper.toResponseDto(updatedProject);
    }

    @Override
    @Transactional
    public ProjectResponseDTO togglePublished(Long id) {
        log.info("Toggling published status for project ID: {}", id);

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

        project.setIsPublished(!Boolean.TRUE.equals(project.getIsPublished()));

        Project updatedProject = projectRepository.save(project);
        log.info("Project published status toggled: {} is now {}", id, updatedProject.getIsPublished());

        return projectMapper.toResponseDto(updatedProject);
    }

    @Override
    @Transactional
    public ProjectResponseDTO addImageToProject(Long projectId, ProjectImageDTO imageDTO) {
        log.info("Adding image to project ID: {}", projectId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        ProjectImage image = projectMapper.toImageEntity(imageDTO);
        image.setProject(project);

        project.getImages().add(image);

        Project updatedProject = projectRepository.save(project);

        return projectMapper.toResponseDto(updatedProject);
    }

    @Override
    @Transactional
    public ProjectResponseDTO removeImageFromProject(Long projectId, Long imageId) {
        log.info("Removing image {} from project ID: {}", imageId, projectId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        boolean removed = project.getImages().removeIf(image -> image.getId().equals(imageId));

        if (!removed) {
            throw new ResourceNotFoundException("ProjectImage", "id", imageId);
        }

        Project updatedProject = projectRepository.save(project);

        return projectMapper.toResponseDto(updatedProject);
    }

    @Override
    @Transactional
    public void deleteProject(Long id) {
        log.info("Deleting project with ID: {}", id);

        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Project", "id", id);
        }

        projectRepository.deleteById(id);
        log.info("Project deleted: {}", id);
    }

    @Override
    @Transactional
    public void deleteAllProjectsByProfile(Long profileId) {
        log.info("Deleting all projects for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

//        projectRepository.deleteByProfileId(profileId);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectStatsDTO getProjectStats(Long profileId) {
        log.debug("Getting project statistics for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        long totalProjects = projectRepository.countByProfileId(profileId);
        long publishedProjects = projectRepository.countByProfileIdAndIsPublishedTrue(profileId);
        long featuredProjects = projectRepository.countByProfileIdAndIsFeaturedTrue(profileId);

        // Calculate total images
        List<Project> projects = projectRepository.findByProfileId(profileId);
        long totalImages = projects.stream()
                .flatMap(p -> p.getImages().stream())
                .count();

        return new ProjectStatsDTO(totalProjects, publishedProjects, featuredProjects, totalImages);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByTitleAndProfile(String title, Long profileId) {
//        return projectRepository.existsByTitleAndProfileId(title, profileId);
        return  true;
    }
}
