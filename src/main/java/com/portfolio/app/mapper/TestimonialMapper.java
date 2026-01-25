package com.portfolio.app.mapper;

import com.portfolio.app.dto.TestimonialRequestDTO;
import com.portfolio.app.dto.TestimonialResponseDTO;
import com.portfolio.app.entity.Profile;
import com.portfolio.app.entity.Testimonial;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TestimonialMapper {

    /* =========================
       DTO → ENTITY
       ========================= */

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profile", source = "profile")
    @Mapping(target = "isFeatured",
            expression = "java(dto.getIsFeatured() != null ? dto.getIsFeatured() : false)")
    @Mapping(target = "isApproved",
            expression = "java(dto.getIsApproved() != null ? dto.getIsApproved() : true)")
    Testimonial toEntity(TestimonialRequestDTO dto, @Context Profile profile);

    /* =========================
       ENTITY → DTO
       ========================= */

    @Mapping(target = "profileId", source = "profile.id")
    @Mapping(target = "profileName", source = "profile.fullName")
    TestimonialResponseDTO toDto(Testimonial testimonial);

    /* =========================
       UPDATE EXISTING ENTITY
       ========================= */

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "profile", source = "profile")
    void updateEntityFromDto(
            TestimonialRequestDTO dto,
            @MappingTarget Testimonial testimonial,
            @Context Profile profile
    );

    /* =========================
       CONTEXT MAPPING
       ========================= */

    @AfterMapping
    default void setProfile(
            @MappingTarget Testimonial testimonial,
            @Context Profile profile
    ) {
        if (profile != null) {
            testimonial.setProfile(profile);
        }
    }
}

