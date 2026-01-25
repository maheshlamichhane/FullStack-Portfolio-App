package com.portfolio.app.mapper;


import com.portfolio.app.dto.SocialMediaDTO;
import com.portfolio.app.dto.SocialMediaResponseDTO;
import com.portfolio.app.dto.SocialMediaSummaryDTO;
import com.portfolio.app.entity.SocialMedia;
import com.portfolio.app.entity.Profile;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Map;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface SocialMediaMapper {

    SocialMediaMapper INSTANCE = Mappers.getMapper(SocialMediaMapper.class);

    @Mapping(target = "profile", source = "profileId", qualifiedByName = "profileIdToProfile")
    SocialMedia toEntity(SocialMediaDTO dto);

    @Mapping(target = "profileId", source = "profile.id")
    SocialMediaDTO toDto(SocialMedia entity);

    @Mapping(target = "profileId", source = "profile.id")
    @Mapping(target = "profileName", expression = "java(entity.getProfile().getFirstName() + \" \" + entity.getProfile().getLastName())")
    @Mapping(target = "profileTitle", source = "profile.title")
    @Mapping(target = "platformIcon", expression = "java(getPlatformIcon(entity.getPlatform()))")
    SocialMediaResponseDTO toResponseDto(SocialMedia entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profile", source = "profileId", qualifiedByName = "profileIdToProfile")
    void updateEntityFromDto(SocialMediaDTO dto, @MappingTarget SocialMedia entity);

    SocialMediaSummaryDTO toSummaryDto(SocialMedia entity);

    @Named("profileIdToProfile")
    default Profile profileIdToProfile(Long profileId) {
        if (profileId == null) return null;
        Profile profile = new Profile();
        profile.setId(profileId);
        return profile;
    }

    default String getPlatformIcon(String platform) {
        if (platform == null) return "fa-link";

        Map<String, String> platformIcons = Map.ofEntries(
                Map.entry("github", "fa-github"),
                Map.entry("linkedin", "fa-linkedin"),
                Map.entry("twitter", "fa-twitter"),
                Map.entry("facebook", "fa-facebook"),
                Map.entry("instagram", "fa-instagram"),
                Map.entry("youtube", "fa-youtube"),
                Map.entry("medium", "fa-medium"),
                Map.entry("stack-overflow", "fa-stack-overflow"),
                Map.entry("gitlab", "fa-gitlab"),
                Map.entry("bitbucket", "fa-bitbucket"),
                Map.entry("behance", "fa-behance"),
                Map.entry("dribbble", "fa-dribbble"),
                Map.entry("codepen", "fa-codepen"),
                Map.entry("dev.to", "fa-dev"),
                Map.entry("hackerrank", "fa-hackerrank"),
                Map.entry("leetcode", "fa-code"),
                Map.entry("kaggle", "fa-kaggle"),
                Map.entry("reddit", "fa-reddit"),
                Map.entry("twitch", "fa-twitch"),
                Map.entry("discord", "fa-discord"),
                Map.entry("slack", "fa-slack"),
                Map.entry("telegram", "fa-telegram"),
                Map.entry("whatsapp", "fa-whatsapp"),
                Map.entry("skype", "fa-skype"),
                Map.entry("website", "fa-globe"),
                Map.entry("blog", "fa-blog"),
                Map.entry("portfolio", "fa-briefcase"),
                Map.entry("email", "fa-envelope"),
                Map.entry("phone", "fa-phone")
        );

        String lowercasePlatform = platform.toLowerCase().trim();
        return platformIcons.getOrDefault(lowercasePlatform, "fa-link");
    }
}
