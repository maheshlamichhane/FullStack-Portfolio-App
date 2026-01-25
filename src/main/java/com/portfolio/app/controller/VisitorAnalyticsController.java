package com.portfolio.app.controller;

import com.portfolio.app.annotation.TrackVisit;
import com.portfolio.app.dto.*;
import com.portfolio.app.service.VisitorAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Tag(name = "Visitor Analytics", description = "Visitor analytics tracking and reporting APIs")
public class VisitorAnalyticsController {

    private final VisitorAnalyticsService analyticsService;

    @PostMapping("/track")
    @Operation(summary = "Track a new visitor", description = "Records a new visitor analytics entry")
    public ResponseEntity<VisitorAnalyticsResponseDTO> trackVisit(
            @Valid @RequestBody VisitorAnalyticsRequestDTO analyticsDTO) {
        VisitorAnalyticsResponseDTO response = analyticsService.trackVisit(analyticsDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/track/simple")
    @Operation(summary = "Track visit with minimal data", description = "Track visit using IP, user agent, and page")
    public ResponseEntity<VisitorAnalyticsResponseDTO> trackSimpleVisit(
            @RequestParam String pageVisited,
            @RequestParam Long profileId,
            HttpServletRequest request) {

        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");

        VisitorAnalyticsResponseDTO response = analyticsService.trackVisit(ipAddress, userAgent, pageVisited, profileId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get analytics by ID", description = "Retrieve a specific analytics record by its ID")
    public ResponseEntity<VisitorAnalyticsResponseDTO> getAnalyticsById(@PathVariable Long id) {
        VisitorAnalyticsResponseDTO analytics = analyticsService.getAnalyticsById(id);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping
    @Operation(summary = "Get all analytics", description = "Retrieve all analytics records")
    public ResponseEntity<List<VisitorAnalyticsResponseDTO>> getAllAnalytics() {
        List<VisitorAnalyticsResponseDTO> analytics = analyticsService.getAllAnalytics();
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/profile/{profileId}")
    @Operation(summary = "Get analytics by profile", description = "Retrieve all analytics for a specific profile")
    public ResponseEntity<List<VisitorAnalyticsResponseDTO>> getAnalyticsByProfileId(@PathVariable Long profileId) {
        List<VisitorAnalyticsResponseDTO> analytics = analyticsService.getAnalyticsByProfileId(profileId);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/profile/{profileId}/summary")
    @Operation(summary = "Get analytics summary", description = "Get comprehensive analytics summary for a profile")
    public ResponseEntity<AnalyticsSummaryDTO> getAnalyticsSummary(@PathVariable Long profileId) {
        AnalyticsSummaryDTO summary = analyticsService.getAnalyticsSummary(profileId);
        return ResponseEntity.ok(summary);
    }

    @PostMapping("/profile/{profileId}/summary/range")
    @Operation(summary = "Get analytics summary by date range", description = "Get analytics summary for a specific date range")
    public ResponseEntity<AnalyticsSummaryDTO> getAnalyticsSummaryByDateRange(
            @PathVariable Long profileId,
            @Valid @RequestBody DateRangeRequestDTO dateRange) {
        AnalyticsSummaryDTO summary = analyticsService.getAnalyticsSummaryByDateRange(
                profileId, dateRange.getStartDate(), dateRange.getEndDate());
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/profile/{profileId}/by-country")
    @Operation(summary = "Get visits by country", description = "Get breakdown of visits by country")
    public ResponseEntity<List<CountryVisitDTO>> getVisitsByCountry(@PathVariable Long profileId) {
        List<CountryVisitDTO> visits = analyticsService.getVisitsByCountry(profileId);
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/profile/{profileId}/by-device")
    @Operation(summary = "Get visits by device type", description = "Get breakdown of visits by device type")
    public ResponseEntity<List<DeviceStatsDTO>> getVisitsByDeviceType(@PathVariable Long profileId) {
        List<DeviceStatsDTO> visits = analyticsService.getVisitsByDeviceType(profileId);
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/profile/{profileId}/by-browser")
    @Operation(summary = "Get visits by browser", description = "Get breakdown of visits by browser")
    public ResponseEntity<List<Map<String, Object>>> getVisitsByBrowser(@PathVariable Long profileId) {
        List<Map<String, Object>> visits = analyticsService.getVisitsByBrowser(profileId);
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/profile/{profileId}/by-os")
    @Operation(summary = "Get visits by operating system", description = "Get breakdown of visits by operating system")
    public ResponseEntity<List<Map<String, Object>>> getVisitsByOperatingSystem(@PathVariable Long profileId) {
        List<Map<String, Object>> visits = analyticsService.getVisitsByOperatingSystem(profileId);
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/profile/{profileId}/by-page")
    @Operation(summary = "Get visits by page", description = "Get breakdown of visits by page visited")
    public ResponseEntity<List<Map<String, Object>>> getVisitsByPage(@PathVariable Long profileId) {
        List<Map<String, Object>> visits = analyticsService.getVisitsByPage(profileId);
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/profile/{profileId}/by-referrer")
    @Operation(summary = "Get visits by referrer", description = "Get breakdown of visits by referrer")
    public ResponseEntity<List<Map<String, Object>>> getVisitsByReferrer(@PathVariable Long profileId) {
        List<Map<String, Object>> visits = analyticsService.getVisitsByReferrer(profileId);
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/profile/{profileId}/daily-visits")
    @Operation(summary = "Get daily visits", description = "Get daily visit counts for a date range")
    public ResponseEntity<List<Map<String, Object>>> getDailyVisits(
            @PathVariable Long profileId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Map<String, Object>> dailyVisits = analyticsService.getDailyVisits(profileId, startDate, endDate);
        return ResponseEntity.ok(dailyVisits);
    }

    @GetMapping("/profile/{profileId}/stats")
    @Operation(summary = "Get basic statistics", description = "Get basic visitor statistics")
    public ResponseEntity<Map<String, Object>> getBasicStats(@PathVariable Long profileId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalVisits", analyticsService.getTotalVisits(profileId));
        stats.put("uniqueVisits", analyticsService.getUniqueVisits(profileId));
        stats.put("averageTimeSpent", analyticsService.getAverageTimeSpent(profileId));

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/profile/{profileId}/recent")
    @Operation(summary = "Get recent visits", description = "Get most recent visits for a profile")
    public ResponseEntity<List<VisitorAnalyticsResponseDTO>> getRecentVisits(
            @PathVariable Long profileId,
            @RequestParam(defaultValue = "10") int limit) {
        List<VisitorAnalyticsResponseDTO> recentVisits = analyticsService.getRecentVisits(profileId, limit);
        return ResponseEntity.ok(recentVisits);
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get analytics by date range", description = "Get analytics records for a specific date range")
    public ResponseEntity<List<VisitorAnalyticsResponseDTO>> getAnalyticsByDateRange(
            @RequestParam Long profileId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<VisitorAnalyticsResponseDTO> analytics = analyticsService.getAnalyticsByDateRange(profileId, start, end);
        return ResponseEntity.ok(analytics);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete analytics record", description = "Delete a specific analytics record")
    public ResponseEntity<Void> deleteAnalytics(@PathVariable Long id) {
        analyticsService.deleteAnalytics(id);
        return ResponseEntity.noContent().build();
    }

    // Example endpoint with automatic tracking using AOP
    @GetMapping("/tracked-endpoint")
    @TrackVisit
    @Operation(summary = "Example tracked endpoint", description = "This endpoint automatically tracks visits using AOP")
    public ResponseEntity<String> exampleTrackedEndpoint() {
        return ResponseEntity.ok("This endpoint is being tracked!");
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
