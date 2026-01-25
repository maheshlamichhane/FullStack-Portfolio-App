package com.portfolio.app.mapper;

import com.portfolio.app.dto.CertificationDTO;
import com.portfolio.app.dto.CertificationResponseDTO;
import com.portfolio.app.dto.CertificationSummaryDTO;
import com.portfolio.app.entity.Certification;
import com.portfolio.app.entity.Profile;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CertificationMapper {

    CertificationMapper INSTANCE = Mappers.getMapper(CertificationMapper.class);

    @Mapping(target = "profile", source = "profileId", qualifiedByName = "profileIdToProfile")
    Certification toEntity(CertificationDTO dto);

    @Mapping(target = "profileId", source = "profile.id")
    CertificationDTO toDto(Certification entity);

    @Mapping(target = "profileId", source = "profile.id")
    @Mapping(target = "profileName", expression = "java(entity.getProfile().getFirstName() + \" \" + entity.getProfile().getLastName())")
    @Mapping(target = "profileTitle", source = "profile.title")
    @Mapping(target = "isExpired", expression = "java(isCertificationExpired(entity))")
    @Mapping(target = "validityMonths", expression = "java(calculateValidityMonths(entity))")
    @Mapping(target = "daysUntilExpiration", expression = "java(calculateDaysUntilExpiration(entity))")
    CertificationResponseDTO toResponseDto(Certification entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profile", source = "profileId", qualifiedByName = "profileIdToProfile")
    void updateEntityFromDto(CertificationDTO dto, @MappingTarget Certification entity);

    @Mapping(target = "isExpired", expression = "java(isCertificationExpired(entity))")
    CertificationSummaryDTO toSummaryDto(Certification entity);

    @Named("profileIdToProfile")
    default Profile profileIdToProfile(Long profileId) {
        if (profileId == null) return null;
        Profile profile = new Profile();
        profile.setId(profileId);
        return profile;
    }

    default Boolean isCertificationExpired(Certification certification) {
        if (Boolean.TRUE.equals(certification.getDoesNotExpire())) {
            return false;
        }
        if (certification.getExpirationDate() == null) {
            return null;
        }
        return certification.getExpirationDate().isBefore(LocalDate.now());
    }

    default Integer calculateValidityMonths(Certification certification) {
        if (certification.getIssueDate() == null) return 0;
        if (Boolean.TRUE.equals(certification.getDoesNotExpire())) return null;
        if (certification.getExpirationDate() == null) return 0;

        return (int) ChronoUnit.MONTHS.between(
                certification.getIssueDate().withDayOfMonth(1),
                certification.getExpirationDate().withDayOfMonth(1)
        );
    }

    default Integer calculateDaysUntilExpiration(Certification certification) {
        if (Boolean.TRUE.equals(certification.getDoesNotExpire())) return null;
        if (certification.getExpirationDate() == null) return null;

        long days = ChronoUnit.DAYS.between(LocalDate.now(), certification.getExpirationDate());
        return (int) days;
    }
}
