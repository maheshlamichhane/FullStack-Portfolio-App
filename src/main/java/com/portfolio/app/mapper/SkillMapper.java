package com.portfolio.app.mapper;

import com.portfolio.app.dto.SkillDTO;
import com.portfolio.app.dto.SkillResponseDTO;
import com.portfolio.app.dto.SkillSummaryDTO;
import com.portfolio.app.entity.Skill;
import com.portfolio.app.entity.Profile;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface SkillMapper {

    SkillMapper INSTANCE = Mappers.getMapper(SkillMapper.class);

    @Mapping(target = "profile", source = "profileId", qualifiedByName = "profileIdToProfile")
    Skill toEntity(SkillDTO dto);

    @Mapping(target = "profileId", source = "profile.id")
    SkillDTO toDto(Skill entity);

    @Mapping(target = "profileId", source = "profile.id")
    @Mapping(target = "profileName", expression = "java(entity.getProfile().getFirstName() + \" \" + entity.getProfile().getLastName())")
    @Mapping(target = "profileTitle", source = "profile.title")
    @Mapping(target = "proficiencyLevel", expression = "java(getProficiencyLevel(entity.getProficiency()))")
    SkillResponseDTO toResponseDto(Skill entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profile", source = "profileId", qualifiedByName = "profileIdToProfile")
    void updateEntityFromDto(SkillDTO dto, @MappingTarget Skill entity);

    SkillSummaryDTO toSummaryDto(Skill entity);

    @Named("profileIdToProfile")
    default Profile profileIdToProfile(Long profileId) {
        if (profileId == null) return null;
        Profile profile = new Profile();
        profile.setId(profileId);
        return profile;
    }

    default String getProficiencyLevel(Integer proficiency) {
        if (proficiency == null) return "Unknown";
        if (proficiency <= 25) return "Beginner";
        if (proficiency <= 50) return "Intermediate";
        if (proficiency <= 75) return "Advanced";
        return "Expert";
    }
}
