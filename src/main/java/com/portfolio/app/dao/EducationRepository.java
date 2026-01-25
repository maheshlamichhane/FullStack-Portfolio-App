package com.portfolio.app.dao;

import com.portfolio.app.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EducationRepository extends JpaRepository<Education, Long> {

    List<Education> findByProfileId(Long profileId);
    List<Education> findByProfileIdOrderByStartDateDesc(Long profileId);
    List<Education> findByProfileIdAndIsCurrentTrue(Long profileId);
    List<Education> findByProfileIdAndInstitutionContainingIgnoreCase(Long profileId, String institution);
    List<Education> findByProfileIdAndDegreeContainingIgnoreCase(Long profileId, String degree);

    @Query("SELECT e FROM Education e WHERE e.profile.id = :profileId AND " +
            "(:fieldOfStudy IS NULL OR LOWER(e.fieldOfStudy) LIKE LOWER(CONCAT('%', :fieldOfStudy, '%')))")
    List<Education> findByProfileIdAndFieldOfStudy(
            @Param("profileId") Long profileId,
            @Param("fieldOfStudy") String fieldOfStudy);

    long countByProfileId(Long profileId);

    @Query("SELECT DISTINCT e.institution FROM Education e WHERE e.profile.id = :profileId")
    List<String> findDistinctInstitutions(@Param("profileId") Long profileId);

    @Query("SELECT DISTINCT e.degree FROM Education e WHERE e.profile.id = :profileId")
    List<String> findDistinctDegrees(@Param("profileId") Long profileId);
}
