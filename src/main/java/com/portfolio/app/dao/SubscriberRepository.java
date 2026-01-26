package com.portfolio.app.dao;

import com.portfolio.app.entity.Subscriber;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {

    // Find subscriber by email and profile
    Optional<Subscriber> findByEmailAndProfileId(String email, Long profileId);

    // Find active subscribers by profile
    List<Subscriber> findByProfileIdAndIsActiveTrue(Long profileId);

    // Find confirmed subscribers by profile
    List<Subscriber> findByProfileIdAndIsConfirmedTrue(Long profileId);

    // Find unconfirmed subscribers by profile
    List<Subscriber> findByProfileIdAndIsConfirmedFalse(Long profileId);

    // Find subscribers by source
    List<Subscriber> findByProfileIdAndSubscriptionSource(Long profileId, String subscriptionSource);

    // Find subscribers who subscribed within date range
    List<Subscriber> findByProfileIdAndSubscribedAtBetween(Long profileId, LocalDateTime start, LocalDateTime end);

    // Find subscribers by confirmation token
    Optional<Subscriber> findByConfirmationToken(String confirmationToken);

    // Find subscribers by email containing (case-insensitive)
    @Query("SELECT s FROM Subscriber s WHERE s.profile.id = :profileId AND " +
            "LOWER(s.email) LIKE LOWER(CONCAT('%', :email, '%'))")
    List<Subscriber> findByProfileIdAndEmailContaining(
            @Param("profileId") Long profileId,
            @Param("email") String email);

    // Find subscribers by name containing (case-insensitive)
    @Query("SELECT s FROM Subscriber s WHERE s.profile.id = :profileId AND " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Subscriber> findByProfileIdAndNameContaining(
            @Param("profileId") Long profileId,
            @Param("name") String name);

    // Check if email exists for profile
    boolean existsByEmailAndProfileId(String email, Long profileId);

    // Get recent subscribers (ordered by subscription date)
    List<Subscriber> findByProfileIdOrderBySubscribedAtDesc(Long profileId);

    // Get paginated subscribers
    Page<Subscriber> findByProfileId(Long profileId, Pageable pageable);

    // Get subscribers with pagination and filtering
    @Query("SELECT s FROM Subscriber s WHERE s.profile.id = :profileId AND " +
            "(:active IS NULL OR s.isActive = :active) AND " +
            "(:confirmed IS NULL OR s.isConfirmed = :confirmed) AND " +
            "(:source IS NULL OR s.subscriptionSource = :source)")
    Page<Subscriber> findByProfileIdWithFilters(
            @Param("profileId") Long profileId,
            @Param("active") Boolean active,
            @Param("confirmed") Boolean confirmed,
            @Param("source") String source,
            Pageable pageable);

    // Count subscribers by profile and status
    long countByProfileId(Long profileId);
    long countByProfileIdAndIsActive(Long profileId, Boolean isActive);
    long countByProfileIdAndIsConfirmed(Long profileId, Boolean isConfirmed);
    long countByProfileIdAndSubscriptionSource(Long profileId, String subscriptionSource);

    // Count subscribers who subscribed today
//    @Query("SELECT COUNT(s) FROM Subscriber s WHERE s.profile.id = :profileId AND " +
//            "DATE(s.subscribedAt) = CURRENT_DATE")
//    long countTodaySubscribers(@Param("profileId") Long profileId);

    // Count subscribers who subscribed this week
    @Query("SELECT COUNT(s) FROM Subscriber s WHERE s.profile.id = :profileId AND " +
            "s.subscribedAt >= :startOfWeek")
    long countWeekSubscribers(@Param("profileId") Long profileId, @Param("startOfWeek") LocalDateTime startOfWeek);

    // Count subscribers who subscribed this month
    @Query("SELECT COUNT(s) FROM Subscriber s WHERE s.profile.id = :profileId AND " +
            "s.subscribedAt >= :startOfMonth")
    long countMonthSubscribers(@Param("profileId") Long profileId, @Param("startOfMonth") LocalDateTime startOfMonth);

    // Get subscription source distribution
    @Query("SELECT s.subscriptionSource, COUNT(s) FROM Subscriber s " +
            "WHERE s.profile.id = :profileId AND s.subscriptionSource IS NOT NULL " +
            "GROUP BY s.subscriptionSource")
    List<Object[]> countSubscribersBySource(@Param("profileId") Long profileId);

    // Get growth rate (new subscribers last 30 days vs previous 30 days)
    @Query("SELECT " +
            "(SELECT COUNT(s) FROM Subscriber s WHERE s.profile.id = :profileId AND " +
            "s.subscribedAt BETWEEN :currentStart AND :currentEnd) as current, " +
            "(SELECT COUNT(s) FROM Subscriber s WHERE s.profile.id = :profileId AND " +
            "s.subscribedAt BETWEEN :previousStart AND :previousEnd) as previous")
    Object[] getGrowthRateData(
            @Param("profileId") Long profileId,
            @Param("currentStart") LocalDateTime currentStart,
            @Param("currentEnd") LocalDateTime currentEnd,
            @Param("previousStart") LocalDateTime previousStart,
            @Param("previousEnd") LocalDateTime previousEnd);

    // Find subscribers to send newsletter to
    @Query("SELECT s FROM Subscriber s WHERE s.profile.id = :profileId AND " +
            "s.isActive = true AND s.isConfirmed = true")
    List<Subscriber> findNewsletterRecipients(@Param("profileId") Long profileId);

    // Find subscribers who haven't confirmed within X days
    @Query("SELECT s FROM Subscriber s WHERE s.profile.id = :profileId AND " +
            "s.isConfirmed = false AND s.subscribedAt < :cutoffDate")
    List<Subscriber> findUnconfirmedSubscribersBefore(
            @Param("profileId") Long profileId,
            @Param("cutoffDate") LocalDateTime cutoffDate);

    // Search subscribers by multiple fields
    @Query("SELECT s FROM Subscriber s WHERE s.profile.id = :profileId AND " +
            "(LOWER(s.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(s.subscriptionSource) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Subscriber> searchSubscribers(
            @Param("profileId") Long profileId,
            @Param("query") String query);
}
