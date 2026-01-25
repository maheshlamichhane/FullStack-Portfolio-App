package com.portfolio.app.dao;

import com.portfolio.app.entity.Achievement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {

    // Find all achievements by profile
    List<Achievement> findByProfileId(Long profileId);

    // Find achievements by profile with pagination
    Page<Achievement> findByProfileId(Long profileId, Pageable pageable);

    // Find achievements by category
    List<Achievement> findByProfileIdAndCategory(Long profileId, String category);

    // Find featured achievements
    List<Achievement> findByProfileIdAndIsFeaturedTrue(Long profileId);

    // Find achievements by organization
    List<Achievement> findByProfileIdAndIssuingOrganization(Long profileId, String issuingOrganization);

    // Find achievements within date range
    List<Achievement> findByProfileIdAndDateReceivedBetween(Long profileId, LocalDate startDate, LocalDate endDate);

    // Find achievements by year
    @Query("SELECT a FROM Achievement a WHERE a.profile.id = :profileId AND " +
            "YEAR(a.dateReceived) = :year")
    List<Achievement> findByProfileIdAndYear(
            @Param("profileId") Long profileId,
            @Param("year") Integer year);

    // Find recent achievements (ordered by date)
    List<Achievement> findByProfileIdOrderByDateReceivedDesc(Long profileId);

    // Find oldest achievements
    List<Achievement> findByProfileIdOrderByDateReceivedAsc(Long profileId);

    // Find achievements by title (case-insensitive search)
    @Query("SELECT a FROM Achievement a WHERE a.profile.id = :profileId AND " +
            "LOWER(a.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Achievement> findByProfileIdAndTitleContaining(
            @Param("profileId") Long profileId,
            @Param("title") String title);

    // Find achievements by multiple categories
    @Query("SELECT a FROM Achievement a WHERE a.profile.id = :profileId AND " +
            "a.category IN :categories")
    List<Achievement> findByProfileIdAndCategoriesIn(
            @Param("profileId") Long profileId,
            @Param("categories") List<String> categories);

    // Get distinct categories
    @Query("SELECT DISTINCT a.category FROM Achievement a WHERE a.profile.id = :profileId")
    List<String> findDistinctCategories(@Param("profileId") Long profileId);

    // Get distinct organizations
    @Query("SELECT DISTINCT a.issuingOrganization FROM Achievement a WHERE a.profile.id = :profileId")
    List<String> findDistinctOrganizations(@Param("profileId") Long profileId);

    // Get achievements count by category
    @Query("SELECT a.category, COUNT(a) FROM Achievement a " +
            "WHERE a.profile.id = :profileId GROUP BY a.category")
    List<Object[]> countAchievementsByCategory(@Param("profileId") Long profileId);

    // Get achievements count by year
    @Query("SELECT YEAR(a.dateReceived), COUNT(a) FROM Achievement a " +
            "WHERE a.profile.id = :profileId GROUP BY YEAR(a.dateReceived) ORDER BY YEAR(a.dateReceived) DESC")
    List<Object[]> countAchievementsByYear(@Param("profileId") Long profileId);

    // Get recent achievements (limited)
    @Query("SELECT a FROM Achievement a WHERE a.profile.id = :profileId " +
            "ORDER BY a.dateReceived DESC")
    List<Achievement> findRecentAchievements(@Param("profileId") Long profileId, Pageable pageable);

    // Search achievements by multiple fields
    @Query("SELECT a FROM Achievement a WHERE a.profile.id = :profileId AND " +
            "(LOWER(a.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(a.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(a.issuingOrganization) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(a.category) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Achievement> searchAchievements(
            @Param("profileId") Long profileId,
            @Param("query") String query);

    // Get latest achievement
    @Query("SELECT a FROM Achievement a WHERE a.profile.id = :profileId " +
            "ORDER BY a.dateReceived DESC LIMIT 1")
    Achievement findLatestAchievement(@Param("profileId") Long profileId);

    // Get achievements for timeline (grouped by year and month)
    @Query("SELECT a FROM Achievement a WHERE a.profile.id = :profileId " +
            "ORDER BY YEAR(a.dateReceived) DESC, MONTH(a.dateReceived) DESC")
    List<Achievement> findAchievementsForTimeline(@Param("profileId") Long profileId);

    // Count achievements by year
    @Query("SELECT COUNT(a) FROM Achievement a WHERE a.profile.id = :profileId AND " +
            "YEAR(a.dateReceived) = YEAR(CURRENT_DATE)")
    Long countCurrentYearAchievements(@Param("profileId") Long profileId);

    // Find achievements with links (has URL)
    List<Achievement> findByProfileIdAndLinkIsNotNull(Long profileId);

    // Find achievements without links
    List<Achievement> findByProfileIdAndLinkIsNull(Long profileId);
}
