package com.portfolio.app.mapper;

import com.portfolio.app.dto.ProjectDTO;
import com.portfolio.app.dto.ProjectImageDTO;
import com.portfolio.app.dto.ProjectResponseDTO;
import com.portfolio.app.dto.ProjectSummaryDTO;
import com.portfolio.app.entity.Project;
import com.portfolio.app.entity.ProjectImage;
import com.portfolio.app.entity.Profile;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProjectMapper {

    ProjectMapper INSTANCE = Mappers.getMapper(ProjectMapper.class);

    @Mapping(target = "profile", source = "profileId", qualifiedByName = "profileIdToProfile")
    @Mapping(target = "images", ignore = true)
    Project toEntity(ProjectDTO dto);

    @Mapping(target = "profileId", source = "profile.id")
    @Mapping(target = "images", source = "images", qualifiedByName = "imagesToImageDTOs")
    ProjectDTO toDto(Project entity);

    @Mapping(target = "profileId", source = "profile.id")
    @Mapping(target = "profileName", expression = "java(entity.getProfile().getFirstName() + \" \" + entity.getProfile().getLastName())")
    @Mapping(target = "images", source = "images", qualifiedByName = "imagesToImageDTOs")
    ProjectResponseDTO toResponseDto(Project entity);

    ProjectSummaryDTO toSummaryDto(Project entity);

    ProjectImageDTO toImageDto(ProjectImage entity);

    @Mapping(target = "project", source = "projectId", qualifiedByName = "projectIdToProject")
    ProjectImage toImageEntity(ProjectImageDTO dto);

    @Named("profileIdToProfile")
    default Profile profileIdToProfile(Long profileId) {
        if (profileId == null) return null;
        Profile profile = new Profile();
        profile.setId(profileId);
        return profile;
    }

    @Named("projectIdToProject")
    default Project projectIdToProject(Long projectId) {
        if (projectId == null) return null;
        Project project = new Project();
        project.setId(projectId);
        return project;
    }

    @Named("imagesToImageDTOs")
    default List<ProjectImageDTO> imagesToImageDTOs(List<ProjectImage> images) {
        if (images == null) return null;
        return images.stream()
                .map(this::toImageDto)
                .collect(Collectors.toList());
    }

    @AfterMapping
    default void setProjectInImages(@MappingTarget Project project) {
        if (project.getImages() != null) {
            project.getImages().forEach(image -> image.setProject(project));
        }
    }
}
