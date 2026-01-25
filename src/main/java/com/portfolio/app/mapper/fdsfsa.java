package com.portfolio.app.mapper;

import com.portfolio.app.dto.EducationDTO;
import com.portfolio.app.entity.Education;
import com.portfolio.app.entity.Profile;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface EducationMapper {

    EducationMapper INSTANCE = Mappers.getMapper(EducationMapper.class);

    @Mapping(target = "profile", source = "profileId", qualifiedByName = "profileIdToProfile")
    Education toEntity(EducationDTO dto);

    @Mapping(target = "profileId", source = "profile.id")
    EducationDTO toDto(Education entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profile", source = "profileId", qualifiedByName = "profileIdToProfile")
    void updateEntityFromDto(EducationDTO dto, @MappingTarget Education entity);

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
