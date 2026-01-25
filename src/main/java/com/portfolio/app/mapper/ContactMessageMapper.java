package com.portfolio.app.mapper;

import com.portfolio.app.dto.ContactMessageDTO;
import com.portfolio.app.dto.ContactMessageResponseDTO;
import com.portfolio.app.entity.ContactMessage;
import com.portfolio.app.entity.Profile;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ContactMessageMapper {

    ContactMessageMapper INSTANCE = Mappers.getMapper(ContactMessageMapper.class);

    @Mapping(target = "profile", source = "profileId", qualifiedByName = "profileIdToProfile")
    ContactMessage toEntity(ContactMessageDTO dto);

    @Mapping(target = "profileId", source = "profile.id")
    ContactMessageDTO toDto(ContactMessage entity);

    @Mapping(target = "profileId", source = "profile.id")
    @Mapping(target = "profileName", expression = "java(entity.getProfile().getFirstName() + \" \" + entity.getProfile().getLastName())")
    @Mapping(target = "profileEmail", source = "profile.email")
    ContactMessageResponseDTO toResponseDto(ContactMessage entity);

    @Mapping(target = "profile", source = "profileId", qualifiedByName = "profileIdToProfile")
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(ContactMessageDTO dto, @MappingTarget ContactMessage entity);

    @Named("profileIdToProfile")
    default Profile profileIdToProfile(Long profileId) {
        if (profileId == null) return null;
        Profile profile = new Profile();
        profile.setId(profileId);
        return profile;
    }
}
