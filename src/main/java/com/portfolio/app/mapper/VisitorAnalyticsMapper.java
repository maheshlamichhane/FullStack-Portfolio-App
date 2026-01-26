package com.portfolio.app.mapper;

import com.portfolio.app.dto.VisitorAnalyticsRequestDTO;
import com.portfolio.app.dto.VisitorAnalyticsResponseDTO;
import com.portfolio.app.entity.VisitorAnalytics;
import com.portfolio.app.entity.Profile;
import org.springframework.stereotype.Component;

@Component
public class VisitorAnalyticsMapper {

    public VisitorAnalytics toEntity(VisitorAnalyticsRequestDTO dto, Profile profile) {
        if (dto == null) {
            return null;
        }

        VisitorAnalytics analytics = new VisitorAnalytics();
        analytics.setIpAddress(dto.getIpAddress());
        analytics.setUserAgent(dto.getUserAgent());
        analytics.setReferrer(dto.getReferrer());
        analytics.setCountry(dto.getCountry());
        analytics.setCity(dto.getCity());
        analytics.setDeviceType(dto.getDeviceType());
        analytics.setBrowser(dto.getBrowser());
        analytics.setOperatingSystem(dto.getOperatingSystem());
        analytics.setPageVisited(dto.getPageVisited());
        analytics.setVisitedAt(dto.getVisitedAt() != null ? dto.getVisitedAt() : null);
        analytics.setTimeSpentSeconds(dto.getTimeSpentSeconds());
        analytics.setIsUniqueVisit(dto.getIsUniqueVisit() != null ? dto.getIsUniqueVisit() : true);
        analytics.setProfile(profile);

        return analytics;
    }

    public VisitorAnalyticsResponseDTO toDto(VisitorAnalytics analytics) {
        if (analytics == null) {
            return null;
        }

        VisitorAnalyticsResponseDTO dto = new VisitorAnalyticsResponseDTO();
        dto.setId(analytics.getId());
        dto.setIpAddress(analytics.getIpAddress());
        dto.setUserAgent(analytics.getUserAgent());
        dto.setReferrer(analytics.getReferrer());
        dto.setCountry(analytics.getCountry());
        dto.setCity(analytics.getCity());
        dto.setDeviceType(analytics.getDeviceType());
        dto.setBrowser(analytics.getBrowser());
        dto.setOperatingSystem(analytics.getOperatingSystem());
        dto.setPageVisited(analytics.getPageVisited());
        dto.setVisitedAt(analytics.getVisitedAt());
        dto.setTimeSpentSeconds(analytics.getTimeSpentSeconds());
        dto.setIsUniqueVisit(analytics.getIsUniqueVisit());
        dto.setProfileId(analytics.getProfile().getId());

        if (analytics.getProfile() != null) {
//            dto.setProfileName(analytics.getProfile().getFullName());
        }

        return dto;
    }

    public void updateEntityFromDto(VisitorAnalyticsRequestDTO dto, VisitorAnalytics analytics, Profile profile) {
        if (dto == null || analytics == null) {
            return;
        }

        if (dto.getIpAddress() != null) {
            analytics.setIpAddress(dto.getIpAddress());
        }
        if (dto.getUserAgent() != null) {
            analytics.setUserAgent(dto.getUserAgent());
        }
        if (dto.getReferrer() != null) {
            analytics.setReferrer(dto.getReferrer());
        }
        if (dto.getCountry() != null) {
            analytics.setCountry(dto.getCountry());
        }
        if (dto.getCity() != null) {
            analytics.setCity(dto.getCity());
        }
        if (dto.getDeviceType() != null) {
            analytics.setDeviceType(dto.getDeviceType());
        }
        if (dto.getBrowser() != null) {
            analytics.setBrowser(dto.getBrowser());
        }
        if (dto.getOperatingSystem() != null) {
            analytics.setOperatingSystem(dto.getOperatingSystem());
        }
        if (dto.getPageVisited() != null) {
            analytics.setPageVisited(dto.getPageVisited());
        }
        if (dto.getVisitedAt() != null) {
            analytics.setVisitedAt(dto.getVisitedAt());
        }
        if (dto.getTimeSpentSeconds() != null) {
            analytics.setTimeSpentSeconds(dto.getTimeSpentSeconds());
        }
        if (dto.getIsUniqueVisit() != null) {
            analytics.setIsUniqueVisit(dto.getIsUniqueVisit());
        }
        if (profile != null) {
            analytics.setProfile(profile);
        }
    }
}
