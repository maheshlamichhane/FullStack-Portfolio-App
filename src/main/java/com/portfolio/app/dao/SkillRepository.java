package com.portfolio.app.dao;

import com.portfolio.app.entity.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    // Find all skills by profile
    List<Skill> findByProfileId(Long profileId);

    // Find skills by profile with pagination
    Page<Skill> findByProfileId(Long profileId, Pageable pageable);

    // Find skills by category
    List<Skill> findByProfileIdAndCategory(Long profileId, String category);

    // Find featured skills
    List<Skill> findByProfileIdAndIsFeaturedTrue(Long profileId);

    // Find skills by proficiency range
    List<Skill> findByProfileIdAndProficiencyBetween(Long profileId, Integer min, Integer max);

    // Find skills with high proficiency (>= 80)
    List<Skill> findByProfileIdAndProficiencyGreaterThanEqual(Long profileId, Integer proficiency);

    // Find skills by name (case-insensitive)
    @Query("SELECT s FROM Skill s WHERE s.profile.id = :profileId AND " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Skill> findByProfileIdAndNameContaining(
            @Param("profileId") Long profileId,
            @Param("name") String name);

    // Find skills by multiple categories
    @Query("SELECT s FROM Skill s WHERE s.profile.id = :profileId AND " +
            "s.category IN :categories")
    List<Skill> findByProfileIdAndCategoriesIn(
            @Param("profileId") Long profileId,
            @Param("categories") List<String> categories);

    // Find skills ordered by display order
    List<Skill> findByProfileIdOrderByDisplayOrderAsc(Long profileId);

    // Find skills ordered by proficiency descending
    List<Skill> findByProfileIdOrderByProficiencyDesc(Long profileId);

    // Find skills ordered by years of experience descending
    List<Skill> findByProfileIdOrderByYearsOfExperienceDesc(Long profileId);

    // Find skill by name and profile
    Optional<Skill> findByNameAndProfileId(String name, Long profileId);

    // Check if skill name exists for profile
    boolean existsByNameAndProfileId(String name, Long profileId);

    // Get distinct categories for a profile
    @Query("SELECT DISTINCT s.category FROM Skill s WHERE s.profile.id = :profileId")
    List<String> findDistinctCategoriesByProfileId(@Param("profileId") Long profileId);

    // Get average proficiency by category
    @Query("SELECT s.category, AVG(s.proficiency) FROM Skill s " +
            "WHERE s.profile.id = :profileId GROUP BY s.category")
    List<Object[]> getAverageProficiencyByCategory(@Param("profileId") Long profileId);

    // Get skills with highest proficiency (top N)
    @Query("SELECT s FROM Skill s WHERE s.profile.id = :profileId " +
            "ORDER BY s.proficiency DESC, s.yearsOfExperience DESC")
    List<Skill> findTopSkills(@Param("profileId") Long profileId, Pageable pageable);

    // Get skills count by category
    @Query("SELECT s.category, COUNT(s) FROM Skill s " +
            "WHERE s.profile.id = :profileId GROUP BY s.category")
    List<Object[]> countSkillsByCategory(@Param("profileId") Long profileId);

    // Search skills by multiple criteria
    @Query("SELECT s FROM Skill s WHERE s.profile.id = :profileId AND " +
            "(LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(s.category) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(s.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Skill> searchSkills(
            @Param("profileId") Long profileId,
            @Param("query") String query);

    // Get total years of experience for a profile
    @Query("SELECT COALESCE(SUM(s.yearsOfExperience), 0) FROM Skill s " +
            "WHERE s.profile.id = :profileId AND s.yearsOfExperience IS NOT NULL")
    Integer getTotalYearsOfExperience(@Param("profileId") Long profileId);

    // Get proficiency statistics
    @Query("SELECT MIN(s.proficiency), MAX(s.proficiency), AVG(s.proficiency) " +
            "FROM Skill s WHERE s.profile.id = :profileId")
    Object[] getProficiencyStats(@Param("profileId") Long profileId);
}
