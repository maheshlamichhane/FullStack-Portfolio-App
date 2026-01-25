package com.portfolio.app.service;


import com.portfolio.app.dto.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface VisitorAnalyticsService {

    VisitorAnalyticsResponseDTO trackVisit(VisitorAnalyticsRequestDTO analyticsDTO);

    VisitorAnalyticsResponseDTO getAnalyticsById(Long id);

    List<VisitorAnalyticsResponseDTO> getAllAnalytics();

    List<VisitorAnalyticsResponseDTO> getAnalyticsByProfileId(Long profileId);

    List<VisitorAnalyticsResponseDTO> getAnalyticsByDateRange(Long profileId, LocalDateTime start, LocalDateTime end);

    AnalyticsSummaryDTO getAnalyticsSummary(Long profileId);

    AnalyticsSummaryDTO getAnalyticsSummaryByDateRange(Long profileId, LocalDate startDate, LocalDate endDate);

    List<CountryVisitDTO> getVisitsByCountry(Long profileId);

    List<DeviceStatsDTO> getVisitsByDeviceType(Long profileId);

    List<Map<String, Object>> getVisitsByBrowser(Long profileId);

    List<Map<String, Object>> getVisitsByOperatingSystem(Long profileId);

    List<Map<String, Object>> getVisitsByPage(Long profileId);

    List<Map<String, Object>> getVisitsByReferrer(Long profileId);

    List<Map<String, Object>> getDailyVisits(Long profileId, LocalDate startDate, LocalDate endDate);

    Double getAverageTimeSpent(Long profileId);

    Long getTotalVisits(Long profileId);

    Long getUniqueVisits(Long profileId);

    void deleteAnalytics(Long id);

    List<VisitorAnalyticsResponseDTO> getRecentVisits(Long profileId, int limit);

    // Utility methods for tracking
    VisitorAnalyticsResponseDTO trackVisit(String ipAddress, String userAgent, String pageVisited, Long profileId);
}
