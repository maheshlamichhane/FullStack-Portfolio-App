package com.portfolio.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsSummaryDTO {
    private Long totalVisits;
    private Long uniqueVisits;
    private Double averageTimeSpent;
    private Long todayVisits;
    private Long thisWeekVisits;
    private Long thisMonthVisits;

    // Breakdowns
    private List<Map<String, Object>> visitsByCountry;
    private List<Map<String, Object>> visitsByDevice;
    private List<Map<String, Object>> visitsByBrowser;
    private List<Map<String, Object>> visitsByOS;
    private List<Map<String, Object>> visitsByPage;
    private List<Map<String, Object>> visitsByReferrer;

    // Time series data for charts
    private List<Map<String, Object>> dailyVisits;
}
