package com.portfolio.app.mapper;

import com.portfolio.app.dto.SubscriberDTO;
import com.portfolio.app.dto.SubscriberResponseDTO;
import com.portfolio.app.entity.Subscriber;
import com.portfolio.app.entity.Profile;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface SubscriberMapper {

    SubscriberMapper INSTANCE = Mappers.getMapper(SubscriberMapper.class);

    @Mapping(target = "profile", source = "profileId", qualifiedByName = "profileIdToProfile")
    Subscriber toEntity(SubscriberDTO dto);

    @Mapping(target = "profileId", source = "profile.id")
    SubscriberDTO toDto(Subscriber entity);

    @Mapping(target = "profileId", source = "profile.id")
    @Mapping(target = "profileName", expression = "java(entity.getProfile().getFirstName() + \" \" + entity.getProfile().getLastName())")
    @Mapping(target = "profileTitle", source = "profile.title")
    @Mapping(target = "daysSubscribed", expression = "java(calculateDaysSubscribed(entity.getSubscribedAt(), entity.getIsActive()))")
    SubscriberResponseDTO toResponseDto(Subscriber entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profile", source = "profileId", qualifiedByName = "profileIdToProfile")
    void updateEntityFromDto(SubscriberDTO dto, @MappingTarget Subscriber entity);

    @Named("profileIdToProfile")
    default Profile profileIdToProfile(Long profileId) {
        if (profileId == null) return null;
        Profile profile = new Profile();
        profile.setId(profileId);
        return profile;
    }

    default Long calculateDaysSubscribed(LocalDateTime subscribedAt, Boolean isActive) {
        if (subscribedAt == null || Boolean.FALSE.equals(isActive)) {
            return null;
        }
        return ChronoUnit.DAYS.between(subscribedAt, LocalDateTime.now());
    }
}
