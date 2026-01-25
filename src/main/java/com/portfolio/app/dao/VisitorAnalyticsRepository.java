package com.portfolio.app.dao;

import com.portfolio.app.entity.VisitorAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface VisitorAnalyticsRepository extends JpaRepository<VisitorAnalytics, Long> {

    List<VisitorAnalytics> findByProfileId(Long profileId);

    List<VisitorAnalytics> findByProfileIdAndVisitedAtBetween(Long profileId, LocalDateTime start, LocalDateTime end);

    long countByProfileId(Long profileId);

    long countByProfileIdAndVisitedAtBetween(Long profileId, LocalDateTime start, LocalDateTime end);

    long countDistinctIpAddressByProfileId(Long profileId);

    long countDistinctIpAddressByProfileIdAndVisitedAtBetween(Long profileId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(v) FROM VisitorAnalytics v WHERE v.profile.id = :profileId AND v.isUniqueVisit = true")
    long countUniqueVisitsByProfileId(@Param("profileId") Long profileId);

    @Query("SELECT COUNT(v) FROM VisitorAnalytics v WHERE v.profile.id = :profileId AND v.isUniqueVisit = true AND v.visitedAt BETWEEN :start AND :end")
    long countUniqueVisitsByProfileIdAndDateRange(@Param("profileId") Long profileId,
                                                  @Param("start") LocalDateTime start,
                                                  @Param("end") LocalDateTime end);

    @Query("SELECT v.country, COUNT(v) as count FROM VisitorAnalytics v WHERE v.profile.id = :profileId GROUP BY v.country ORDER BY count DESC")
    List<Object[]> countByCountry(@Param("profileId") Long profileId);

    @Query("SELECT v.city, COUNT(v) as count FROM VisitorAnalytics v WHERE v.profile.id = :profileId AND v.country = :country GROUP BY v.city ORDER BY count DESC")
    List<Object[]> countByCity(@Param("profileId") Long profileId, @Param("country") String country);

    @Query("SELECT v.deviceType, COUNT(v) as count FROM VisitorAnalytics v WHERE v.profile.id = :profileId GROUP BY v.deviceType ORDER BY count DESC")
    List<Object[]> countByDeviceType(@Param("profileId") Long profileId);

    @Query("SELECT v.browser, COUNT(v) as count FROM VisitorAnalytics v WHERE v.profile.id = :profileId GROUP BY v.browser ORDER BY count DESC")
    List<Object[]> countByBrowser(@Param("profileId") Long profileId);

    @Query("SELECT v.operatingSystem, COUNT(v) as count FROM VisitorAnalytics v WHERE v.profile.id = :profileId GROUP BY v.operatingSystem ORDER BY count DESC")
    List<Object[]> countByOperatingSystem(@Param("profileId") Long profileId);

    @Query("SELECT v.pageVisited, COUNT(v) as count FROM VisitorAnalytics v WHERE v.profile.id = :profileId GROUP BY v.pageVisited ORDER BY count DESC")
    List<Object[]> countByPageVisited(@Param("profileId") Long profileId);

    @Query("SELECT FUNCTION('DATE', v.visitedAt) as date, COUNT(v) as count FROM VisitorAnalytics v " +
            "WHERE v.profile.id = :profileId AND v.visitedAt BETWEEN :start AND :end " +
            "GROUP BY FUNCTION('DATE', v.visitedAt) ORDER BY date")
    List<Object[]> countByDateRange(@Param("profileId") Long profileId,
                                    @Param("start") LocalDateTime start,
                                    @Param("end") LocalDateTime end);

    @Query("SELECT AVG(v.timeSpentSeconds) FROM VisitorAnalytics v WHERE v.profile.id = :profileId AND v.timeSpentSeconds IS NOT NULL")
    Double findAverageTimeSpent(@Param("profileId") Long profileId);

    @Query("SELECT AVG(v.timeSpentSeconds) FROM VisitorAnalytics v WHERE v.profile.id = :profileId AND v.timeSpentSeconds IS NOT NULL AND v.visitedAt BETWEEN :start AND :end")
    Double findAverageTimeSpentByDateRange(@Param("profileId") Long profileId,
                                           @Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end);

    @Query("SELECT v FROM VisitorAnalytics v WHERE v.profile.id = :profileId ORDER BY v.visitedAt DESC")
    List<VisitorAnalytics> findRecentVisitsByProfileId(@Param("profileId") Long profileId);

    @Query("SELECT v.referrer, COUNT(v) as count FROM VisitorAnalytics v WHERE v.profile.id = :profileId AND v.referrer IS NOT NULL AND v.referrer != '' GROUP BY v.referrer ORDER BY count DESC")
    List<Object[]> countByReferrer(@Param("profileId") Long profileId);
}
