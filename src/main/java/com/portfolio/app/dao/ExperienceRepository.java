package com.portfolio.app.dao;

import com.portfolio.app.entity.Experience;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Long> {

    // Find all experiences by profile
    List<Experience> findByProfileId(Long profileId);

    // Find experiences by profile with pagination
    Page<Experience> findByProfileId(Long profileId, Pageable pageable);

    // Find current experiences
    List<Experience> findByProfileIdAndIsCurrentTrue(Long profileId);

    // Find past experiences
    List<Experience> findByProfileIdAndIsCurrentFalse(Long profileId);

    // Find experiences by employment type
    List<Experience> findByProfileIdAndEmploymentType(Long profileId, String employmentType);

    // Find experiences within date range
    @Query("SELECT e FROM Experience e WHERE e.profile.id = :profileId AND " +
            "((e.startDate <= :endDate AND (e.endDate >= :startDate OR e.isCurrent = true)) OR " +
            "(e.isCurrent = true AND e.startDate <= :endDate))")
    List<Experience> findByProfileIdAndDateRange(
            @Param("profileId") Long profileId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Find experiences by company name (case-insensitive search)
    @Query("SELECT e FROM Experience e WHERE e.profile.id = :profileId AND " +
            "LOWER(e.company) LIKE LOWER(CONCAT('%', :company, '%'))")
    List<Experience> findByProfileIdAndCompanyContaining(
            @Param("profileId") Long profileId,
            @Param("company") String company);

    // Find experiences containing specific technology
    @Query("SELECT e FROM Experience e WHERE e.profile.id = :profileId AND " +
            ":technology MEMBER OF e.technologies")
    List<Experience> findByProfileIdAndTechnology(
            @Param("profileId") Long profileId,
            @Param("technology") String technology);

    // Find most recent experiences (limited)
    List<Experience> findByProfileIdOrderByStartDateDesc(Long profileId, Pageable pageable);

    // Calculate total experience in months
    @Query("SELECT SUM(CASE " +
            "WHEN e.isCurrent = true THEN TIMESTAMPDIFF(MONTH, e.startDate, CURRENT_DATE) " +
            "ELSE TIMESTAMPDIFF(MONTH, e.startDate, e.endDate) " +
            "END) FROM Experience e WHERE e.profile.id = :profileId")
    Integer calculateTotalExperienceMonths(@Param("profileId") Long profileId);

    // Find overlapping experiences (for validation)
    @Query("SELECT e FROM Experience e WHERE e.profile.id = :profileId AND " +
            "e.id <> :excludeId AND " +
            "((e.startDate <= :endDate AND (e.endDate >= :startDate OR e.isCurrent = true)) OR " +
            "(:isCurrent = true AND e.startDate <= :endDate))")
    List<Experience> findOverlappingExperiences(
            @Param("profileId") Long profileId,
            @Param("excludeId") Long excludeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("isCurrent") Boolean isCurrent);

    // Count experiences by profile
    long countByProfileId(Long profileId);
}
