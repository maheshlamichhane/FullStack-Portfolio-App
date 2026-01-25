package com.portfolio.app.mapper;

import com.portfolio.app.dto.ProfileDTO;
import com.portfolio.app.entity.Profile;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProfileMapper {
    ProfileDTO toDto(Profile profile);
    Profile toEntity(ProfileDTO profileDTO);
    void updateEntity(ProfileDTO profileDTO, @MappingTarget Profile profile);
}
