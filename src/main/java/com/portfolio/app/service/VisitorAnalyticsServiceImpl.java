package com.portfolio.app.service;

import com.portfolio.app.dao.ProfileRepository;
import com.portfolio.app.dao.VisitorAnalyticsRepository;
import com.portfolio.app.dto.*;
import com.portfolio.app.entity.VisitorAnalytics;
import com.portfolio.app.entity.Profile;
import com.portfolio.app.exception.ResourceNotFoundException;
import com.portfolio.app.mapper.VisitorAnalyticsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class VisitorAnalyticsServiceImpl implements VisitorAnalyticsService {

    private final VisitorAnalyticsRepository analyticsRepository;
    private final ProfileRepository profileRepository;
    private final VisitorAnalyticsMapper analyticsMapper;

    private static final String ANALYTICS_NOT_FOUND = "Analytics record not found with id: ";
    private static final String PROFILE_NOT_FOUND = "Profile not found with id: ";

    @Override
    public VisitorAnalyticsResponseDTO trackVisit(VisitorAnalyticsRequestDTO analyticsDTO) {
//        Profile profile = profileRepository.findById(analyticsDTO.getProfileId());
//                .orElseThrow(() -> new ResourceNotFoundException(PROFILE_NOT_FOUND + analyticsDTO.getProfileId()));

        Profile profile = null;
        // Check if this is a unique visit (by IP and day)
        boolean isUnique = true;
        if (analyticsDTO.getIpAddress() != null) {
            LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
            LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX);
            long existingVisits = analyticsRepository.countByProfileIdAndVisitedAtBetween(
                    analyticsDTO.getProfileId(), startOfDay, endOfDay);

            // Check for same IP visit today
            List<VisitorAnalytics> todayVisits = analyticsRepository.findByProfileIdAndVisitedAtBetween(
                    analyticsDTO.getProfileId(), startOfDay, endOfDay);
            isUnique = todayVisits.stream()
                    .noneMatch(v -> v.getIpAddress() != null &&
                            v.getIpAddress().equals(analyticsDTO.getIpAddress()));
        }

        VisitorAnalytics analytics = analyticsMapper.toEntity(analyticsDTO, profile);
        analytics.setIsUniqueVisit(isUnique);

        // Set current time if not provided
        if (analytics.getVisitedAt() == null) {
            analytics.setVisitedAt(LocalDateTime.now());
        }

        VisitorAnalytics savedAnalytics = analyticsRepository.save(analytics);
        log.info("Tracked visit for profile {}: {}", analyticsDTO.getProfileId(), analyticsDTO.getPageVisited());

        return analyticsMapper.toDto(savedAnalytics);
    }

    @Override
    public VisitorAnalyticsResponseDTO getAnalyticsById(Long id) {
        return null;
    }

//    @Override
//    @Transactional(readOnly = true)
//    public VisitorAnalyticsResponseDTO getAnalyticsById(Long id) {
//        VisitorAnalytics analytics = analyticsRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException(ANALYTICS_NOT_FOUND + id));
//        return analyticsMapper.toDto(analytics);
//    }

    @Override
    @Transactional(readOnly = true)
    public List<VisitorAnalyticsResponseDTO> getAllAnalytics() {
        return analyticsRepository.findAll().stream()
                .map(analyticsMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VisitorAnalyticsResponseDTO> getAnalyticsByProfileId(Long profileId) {
        return analyticsRepository.findByProfileId(profileId).stream()
                .map(analyticsMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VisitorAnalyticsResponseDTO> getAnalyticsByDateRange(Long profileId, LocalDateTime start, LocalDateTime end) {
        return analyticsRepository.findByProfileIdAndVisitedAtBetween(profileId, start, end).stream()
                .map(analyticsMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AnalyticsSummaryDTO getAnalyticsSummary(Long profileId) {
        AnalyticsSummaryDTO summary = new AnalyticsSummaryDTO();

        // Total visits
        summary.setTotalVisits(analyticsRepository.countByProfileId(profileId));

        // Unique visits
        summary.setUniqueVisits(analyticsRepository.countUniqueVisitsByProfileId(profileId));

        // Average time spent
        Double avgTime = analyticsRepository.findAverageTimeSpent(profileId);
        summary.setAverageTimeSpent(avgTime != null ? avgTime : 0.0);

        // Today's visits
        LocalDateTime todayStart = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.now().with(LocalTime.MAX);
        summary.setTodayVisits(analyticsRepository.countByProfileIdAndVisitedAtBetween(profileId, todayStart, todayEnd));

        // This week's visits
        LocalDateTime weekStart = LocalDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).with(LocalTime.MIN);
        summary.setThisWeekVisits(analyticsRepository.countByProfileIdAndVisitedAtBetween(profileId, weekStart, todayEnd));

        // This month's visits
        LocalDateTime monthStart = LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);
        summary.setThisMonthVisits(analyticsRepository.countByProfileIdAndVisitedAtBetween(profileId, monthStart, todayEnd));

        // Breakdowns
        summary.setVisitsByCountry(convertObjectArrayToList(analyticsRepository.countByCountry(profileId)));
        summary.setVisitsByDevice(convertObjectArrayToList(analyticsRepository.countByDeviceType(profileId)));
        summary.setVisitsByBrowser(convertObjectArrayToList(analyticsRepository.countByBrowser(profileId)));
        summary.setVisitsByOS(convertObjectArrayToList(analyticsRepository.countByOperatingSystem(profileId)));
        summary.setVisitsByPage(convertObjectArrayToList(analyticsRepository.countByPageVisited(profileId)));
        summary.setVisitsByReferrer(convertObjectArrayToList(analyticsRepository.countByReferrer(profileId)));

        // Last 30 days daily visits
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30).with(LocalTime.MIN);
        summary.setDailyVisits(convertObjectArrayToList(analyticsRepository.countByDateRange(profileId, thirtyDaysAgo, todayEnd)));

        return summary;
    }

    @Override
    @Transactional(readOnly = true)
    public AnalyticsSummaryDTO getAnalyticsSummaryByDateRange(Long profileId, LocalDate startDate, LocalDate endDate) {
        AnalyticsSummaryDTO summary = new AnalyticsSummaryDTO();

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        // Total visits
        summary.setTotalVisits(analyticsRepository.countByProfileIdAndVisitedAtBetween(profileId, start, end));

        // Unique visits
        summary.setUniqueVisits(analyticsRepository.countUniqueVisitsByProfileIdAndDateRange(profileId, start, end));

        // Average time spent
        Double avgTime = analyticsRepository.findAverageTimeSpentByDateRange(profileId, start, end);
        summary.setAverageTimeSpent(avgTime != null ? avgTime : 0.0);

        // Breakdowns
        summary.setVisitsByCountry(convertObjectArrayToList(analyticsRepository.countByCountry(profileId)));
        summary.setVisitsByDevice(convertObjectArrayToList(analyticsRepository.countByDeviceType(profileId)));
        summary.setVisitsByBrowser(convertObjectArrayToList(analyticsRepository.countByBrowser(profileId)));
        summary.setVisitsByOS(convertObjectArrayToList(analyticsRepository.countByOperatingSystem(profileId)));
        summary.setVisitsByPage(convertObjectArrayToList(analyticsRepository.countByPageVisited(profileId)));
        summary.setVisitsByReferrer(convertObjectArrayToList(analyticsRepository.countByReferrer(profileId)));

        // Daily visits for the date range
        summary.setDailyVisits(convertObjectArrayToList(analyticsRepository.countByDateRange(profileId, start, end)));

        return summary;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CountryVisitDTO> getVisitsByCountry(Long profileId) {
        List<Object[]> results = analyticsRepository.countByCountry(profileId);
        long totalVisits = analyticsRepository.countByProfileId(profileId);

        return results.stream()
                .map(result -> {
                    String country = (String) result[0];
                    Long count = (Long) result[1];
                    double percentage = totalVisits > 0 ? (count.doubleValue() / totalVisits) * 100 : 0;

                    return new CountryVisitDTO(
                            country != null ? country : "Unknown",
                            count,
                            Math.round(percentage * 100.0) / 100.0
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeviceStatsDTO> getVisitsByDeviceType(Long profileId) {
        List<Object[]> results = analyticsRepository.countByDeviceType(profileId);
        long totalVisits = analyticsRepository.countByProfileId(profileId);

        return results.stream()
                .map(result -> {
                    String deviceType = (String) result[0];
                    Long count = (Long) result[1];
                    double percentage = totalVisits > 0 ? (count.doubleValue() / totalVisits) * 100 : 0;

                    return new DeviceStatsDTO(
                            deviceType != null ? deviceType : "Unknown",
                            count,
                            Math.round(percentage * 100.0) / 100.0
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getVisitsByBrowser(Long profileId) {
        return convertObjectArrayToList(analyticsRepository.countByBrowser(profileId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getVisitsByOperatingSystem(Long profileId) {
        return convertObjectArrayToList(analyticsRepository.countByOperatingSystem(profileId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getVisitsByPage(Long profileId) {
        return convertObjectArrayToList(analyticsRepository.countByPageVisited(profileId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getVisitsByReferrer(Long profileId) {
        return convertObjectArrayToList(analyticsRepository.countByReferrer(profileId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDailyVisits(Long profileId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        return convertObjectArrayToList(analyticsRepository.countByDateRange(profileId, start, end));
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageTimeSpent(Long profileId) {
        return analyticsRepository.findAverageTimeSpent(profileId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTotalVisits(Long profileId) {
        return analyticsRepository.countByProfileId(profileId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getUniqueVisits(Long profileId) {
        return analyticsRepository.countUniqueVisitsByProfileId(profileId);
    }

    @Override
    public void deleteAnalytics(Long id) {
//        if (!analyticsRepository.existsById(id)) {
//            throw new ResourceNotFoundException(ANALYTICS_NOT_FOUND + id);
//        }
//        analyticsRepository.deleteById(id);
//        log.info("Deleted analytics record with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VisitorAnalyticsResponseDTO> getRecentVisits(Long profileId, int limit) {
        return analyticsRepository.findRecentVisitsByProfileId(profileId).stream()
                .limit(limit)
                .map(analyticsMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public VisitorAnalyticsResponseDTO trackVisit(String ipAddress, String userAgent, String pageVisited, Long profileId) {
        VisitorAnalyticsRequestDTO requestDTO = new VisitorAnalyticsRequestDTO();
        requestDTO.setIpAddress(ipAddress);
        requestDTO.setUserAgent(userAgent);
        requestDTO.setPageVisited(pageVisited);
        requestDTO.setProfileId(profileId);
        requestDTO.setVisitedAt(LocalDateTime.now());

        // Extract browser and OS from user agent
        extractBrowserAndOS(userAgent, requestDTO);

        return trackVisit(requestDTO);
    }

    private void extractBrowserAndOS(String userAgent, VisitorAnalyticsRequestDTO dto) {
        if (userAgent == null || userAgent.isEmpty()) {
            return;
        }

        userAgent = userAgent.toLowerCase();

        // Detect browser
        if (userAgent.contains("chrome") && !userAgent.contains("edg")) {
            dto.setBrowser("Chrome");
        } else if (userAgent.contains("firefox")) {
            dto.setBrowser("Firefox");
        } else if (userAgent.contains("safari") && !userAgent.contains("chrome")) {
            dto.setBrowser("Safari");
        } else if (userAgent.contains("edge")) {
            dto.setBrowser("Edge");
        } else if (userAgent.contains("opera")) {
            dto.setBrowser("Opera");
        } else {
            dto.setBrowser("Other");
        }

        // Detect OS
        if (userAgent.contains("windows")) {
            dto.setOperatingSystem("Windows");
        } else if (userAgent.contains("mac")) {
            dto.setOperatingSystem("macOS");
        } else if (userAgent.contains("linux")) {
            dto.setOperatingSystem("Linux");
        } else if (userAgent.contains("android")) {
            dto.setOperatingSystem("Android");
        } else if (userAgent.contains("iphone") || userAgent.contains("ipad")) {
            dto.setOperatingSystem("iOS");
        } else {
            dto.setOperatingSystem("Other");
        }

        // Detect device type
        if (userAgent.contains("mobile")) {
            dto.setDeviceType("Mobile");
        } else if (userAgent.contains("tablet")) {
            dto.setDeviceType("Tablet");
        } else {
            dto.setDeviceType("Desktop");
        }
    }

    private List<Map<String, Object>> convertObjectArrayToList(List<Object[]> results) {
        return results.stream()
                .map(result -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", result[0] != null ? result[0].toString() : "Unknown");
                    map.put("count", result[1]);
                    return map;
                })
                .collect(Collectors.toList());
    }
}
