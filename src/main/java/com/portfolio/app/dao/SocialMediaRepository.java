package com.portfolio.app.dao;

import com.portfolio.app.entity.SocialMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SocialMediaRepository extends JpaRepository<SocialMedia, Long> {

    // Find all social media links by profile
    List<SocialMedia> findByProfileId(Long profileId);

    // Find visible social media links by profile
    List<SocialMedia> findByProfileIdAndIsVisibleTrue(Long profileId);

    // Find hidden social media links by profile
    List<SocialMedia> findByProfileIdAndIsVisibleFalse(Long profileId);

    // Find social media links by platform
    List<SocialMedia> findByProfileIdAndPlatform(Long profileId, String platform);

    // Find social media links by platform containing (case-insensitive)
    @Query("SELECT sm FROM SocialMedia sm WHERE sm.profile.id = :profileId AND " +
            "LOWER(sm.platform) LIKE LOWER(CONCAT('%', :platform, '%'))")
    List<SocialMedia> findByProfileIdAndPlatformContaining(
            @Param("profileId") Long profileId,
            @Param("platform") String platform);

    // Find social media links by username
    @Query("SELECT sm FROM SocialMedia sm WHERE sm.profile.id = :profileId AND " +
            "LOWER(sm.username) LIKE LOWER(CONCAT('%', :username, '%'))")
    List<SocialMedia> findByProfileIdAndUsernameContaining(
            @Param("profileId") Long profileId,
            @Param("username") String username);

    // Find social media links ordered by display order
    List<SocialMedia> findByProfileIdOrderByDisplayOrderAsc(Long profileId);

    // Find social media links ordered by platform
    List<SocialMedia> findByProfileIdOrderByPlatformAsc(Long profileId);

    // Find social media link by URL
    Optional<SocialMedia> findByUrlAndProfileId(String url, Long profileId);

    // Check if URL exists for profile
    boolean existsByUrlAndProfileId(String url, Long profileId);

    // Check if platform exists for profile (case-insensitive)
    @Query("SELECT CASE WHEN COUNT(sm) > 0 THEN true ELSE false END " +
            "FROM SocialMedia sm WHERE sm.profile.id = :profileId AND " +
            "LOWER(sm.platform) = LOWER(:platform)")
    boolean existsByPlatformAndProfileId(
            @Param("profileId") Long profileId,
            @Param("platform") String platform);

    // Get distinct platforms
    @Query("SELECT DISTINCT sm.platform FROM SocialMedia sm WHERE sm.profile.id = :profileId")
    List<String> findDistinctPlatforms(@Param("profileId") Long profileId);

    // Get platform distribution
    @Query("SELECT sm.platform, COUNT(sm) FROM SocialMedia sm " +
            "WHERE sm.profile.id = :profileId GROUP BY sm.platform")
    List<Object[]> countSocialMediaByPlatform(@Param("profileId") Long profileId);

    // Get next available display order
    @Query("SELECT COALESCE(MAX(sm.displayOrder), 0) + 1 FROM SocialMedia sm WHERE sm.profile.id = :profileId")
    Integer getNextDisplayOrder(@Param("profileId") Long profileId);

    // Find social media links by display order range
    List<SocialMedia> findByProfileIdAndDisplayOrderBetween(Long profileId, Integer start, Integer end);

    // Find social media links with specific display order
    Optional<SocialMedia> findByProfileIdAndDisplayOrder(Long profileId, Integer displayOrder);

    // Search social media links
    @Query("SELECT sm FROM SocialMedia sm WHERE sm.profile.id = :profileId AND " +
            "(LOWER(sm.platform) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(sm.url) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(sm.username) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<SocialMedia> searchSocialMedia(
            @Param("profileId") Long profileId,
            @Param("query") String query);

    // Count visible links
    long countByProfileIdAndIsVisible(Long profileId, Boolean isVisible);

    // Count links by platform
    long countByProfileIdAndPlatform(Long profileId, String platform);
}
