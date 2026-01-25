package com.portfolio.app.mapper;

import com.portfolio.app.dto.BlogPostDTO;
import com.portfolio.app.dto.BlogPostSummaryDTO;
import com.portfolio.app.entity.BlogPost;
import com.portfolio.app.entity.Profile;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        uses = {ProfileMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface BlogPostMapper {

    BlogPostMapper INSTANCE = Mappers.getMapper(BlogPostMapper.class);

    @Mapping(target = "profile", source = "profileId", qualifiedByName = "profileIdToProfile")
    BlogPost toEntity(BlogPostDTO dto);

    @Mapping(target = "profileId", source = "profile.id")
    BlogPostDTO toDto(BlogPost entity);

    BlogPostSummaryDTO toSummaryDto(BlogPost entity);

    @Mapping(target = "profile", source = "profileId", qualifiedByName = "profileIdToProfile")
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(BlogPostDTO dto, @MappingTarget BlogPost entity);

    @Named("profileIdToProfile")
    default Profile profileIdToProfile(Long profileId) {
        if (profileId == null) return null;
        Profile profile = new Profile();
        profile.setId(profileId);
        return profile;
    }
}
