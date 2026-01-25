package com.portfolio.app.mapper;

import com.portfolio.app.dto.AchievementDTO;
import com.portfolio.app.dto.AchievementResponseDTO;
import com.portfolio.app.dto.AchievementSummaryDTO;
import com.portfolio.app.dto.AchievementTimelineDTO;
import com.portfolio.app.entity.Achievement;
import com.portfolio.app.entity.Profile;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.Year;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AchievementMapper {

    AchievementMapper INSTANCE = Mappers.getMapper(AchievementMapper.class);

    @Mapping(target = "profile", source = "profileId", qualifiedByName = "profileIdToProfile")
    Achievement toEntity(AchievementDTO dto);

    @Mapping(target = "profileId", source = "profile.id")
    AchievementDTO toDto(Achievement entity);

    @Mapping(target = "profileId", source = "profile.id")
    @Mapping(target = "profileName", expression = "java(entity.getProfile().getFirstName() + \" \" + entity.getProfile().getLastName())")
    @Mapping(target = "profileTitle", source = "profile.title")
    @Mapping(target = "yearsAgo", expression = "java(calculateYearsAgo(entity.getDateReceived()))")
    AchievementResponseDTO toResponseDto(Achievement entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profile", source = "profileId", qualifiedByName = "profileIdToProfile")
    void updateEntityFromDto(AchievementDTO dto, @MappingTarget Achievement entity);

    AchievementSummaryDTO toSummaryDto(Achievement entity);

    @Mapping(target = "year", expression = "java(entity.getDateReceived().getYear())")
    @Mapping(target = "month", expression = "java(entity.getDateReceived().getMonthValue())")
    AchievementTimelineDTO toTimelineDto(Achievement entity);

    @Named("profileIdToProfile")
    default Profile profileIdToProfile(Long profileId) {
        if (profileId == null) return null;
        Profile profile = new Profile();
        profile.setId(profileId);
        return profile;
    }

    default Integer calculateYearsAgo(LocalDate dateReceived) {
        if (dateReceived == null) return null;
        return Year.now().getValue() - dateReceived.getYear();
    }
}
