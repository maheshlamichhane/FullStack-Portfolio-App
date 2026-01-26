package com.portfolio.app.dao;

import com.portfolio.app.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // Find all projects by profile
    List<Project> findByProfileId(Long profileId);

    // Find projects by profile with pagination
    Page<Project> findByProfileId(Long profileId, Pageable pageable);

    // Find published projects by profile
    Page<Project> findByProfileIdAndIsPublishedTrue(Long profileId, Pageable pageable);

    // Find featured projects
    List<Project> findByProfileIdAndIsFeaturedTrue(Long profileId);

    // Find published featured projects
    List<Project> findByProfileIdAndIsFeaturedTrueAndIsPublishedTrue(Long profileId);

    // Find projects by technology
    @Query("SELECT p FROM Project p WHERE :technology MEMBER OF p.technologies AND p.isPublished = true")
    Page<Project> findByTechnology(@Param("technology") String technology, Pageable pageable);

    // Find projects by multiple technologies
//    @Query("SELECT p FROM Project p WHERE p.technologies IN :technologies AND p.isPublished = true")
//    Page<Project> findByTechnologiesIn(@Param("technologies") List<String> technologies, Pageable pageable);

    // Search projects by title or description
    @Query("SELECT p FROM Project p WHERE " +
            "(LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
            "p.profile.id = :profileId")
    Page<Project> searchByProfileId(
            @Param("profileId") Long profileId,
            @Param("query") String query,
            Pageable pageable);

    // Find projects within date range
    @Query("SELECT p FROM Project p WHERE p.profile.id = :profileId AND " +
            "((p.startDate <= :endDate AND p.endDate >= :startDate) OR " +
            "(p.startDate <= :endDate AND p.endDate IS NULL))")
    Page<Project> findByDateRange(
            @Param("profileId") Long profileId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    // Count projects by profile
    long countByProfileId(Long profileId);

    // Count published projects by profile
    long countByProfileIdAndIsPublishedTrue(Long profileId);

    // Count featured projects by profile
    long countByProfileIdAndIsFeaturedTrue(Long profileId);
}
