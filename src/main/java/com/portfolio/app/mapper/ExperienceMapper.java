package com.portfolio.app.mapper;

import com.portfolio.app.dto.ExperienceDTO;
import com.portfolio.app.dto.ExperienceResponseDTO;
import com.portfolio.app.dto.ExperienceSummaryDTO;
import com.portfolio.app.entity.Experience;
import com.portfolio.app.entity.Profile;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ExperienceMapper {

    ExperienceMapper INSTANCE = Mappers.getMapper(ExperienceMapper.class);

    @Mapping(target = "profile", source = "profileId", qualifiedByName = "profileIdToProfile")
    Experience toEntity(ExperienceDTO dto);

    @Mapping(target = "profileId", source = "profile.id")
    ExperienceDTO toDto(Experience entity);

    @Mapping(target = "profileId", source = "profile.id")
    @Mapping(target = "profileName", expression = "java(entity.getProfile().getFirstName() + \" \" + entity.getProfile().getLastName())")
    @Mapping(target = "durationMonths", expression = "java(calculateDurationMonths(entity.getStartDate(), entity.getEndDate(), entity.getIsCurrent()))")
    ExperienceResponseDTO toResponseDto(Experience entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profile", source = "profileId", qualifiedByName = "profileIdToProfile")
    void updateEntityFromDto(ExperienceDTO dto, @MappingTarget Experience entity);

    ExperienceSummaryDTO toSummaryDto(Experience entity);

    @Named("profileIdToProfile")
    default Profile profileIdToProfile(Long profileId) {
        if (profileId == null) return null;
        Profile profile = new Profile();
        profile.setId(profileId);
        return profile;
    }

    default Integer calculateDurationMonths(LocalDate startDate, LocalDate endDate, Boolean isCurrent) {
        if (startDate == null) return 0;

        LocalDate end = (isCurrent != null && isCurrent) ? LocalDate.now() : endDate;
        if (end == null) return 0;

        return (int) ChronoUnit.MONTHS.between(
                startDate.withDayOfMonth(1),
                end.withDayOfMonth(1)
        );
    }
}
