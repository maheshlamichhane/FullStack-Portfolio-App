package com.portfolio.app.service;

import com.portfolio.app.dto.*;
import java.util.List;
import java.util.Map;

public interface SocialMediaService {

    // Create
    SocialMediaResponseDTO createSocialMedia(SocialMediaDTO socialMediaDTO);

    // Read
    SocialMediaResponseDTO getSocialMediaById(Long id);
    List<SocialMediaResponseDTO> getAllSocialMediaByProfile(Long profileId);
    List<SocialMediaResponseDTO> getVisibleSocialMediaByProfile(Long profileId);
    List<SocialMediaResponseDTO> getSocialMediaByPlatform(Long profileId, String platform);
    List<SocialMediaSummaryDTO> getSocialMediaByDisplayOrder(Long profileId);
    SocialMediaResponseDTO getSocialMediaByUrl(Long profileId, String url);
    List<SocialMediaResponseDTO> searchSocialMedia(Long profileId, String query);

    // Update
    SocialMediaResponseDTO updateSocialMedia(Long id, SocialMediaDTO socialMediaDTO);
    SocialMediaResponseDTO toggleVisibility(Long id);
    SocialMediaResponseDTO updateDisplayOrder(Long id, Integer displayOrder);
    void updateMultipleDisplayOrders(Map<Long, Integer> displayOrderUpdates);

    // Delete
    void deleteSocialMedia(Long id);
    void deleteAllSocialMediaByProfile(Long profileId);
    void deleteSocialMediaByPlatform(Long profileId, String platform);

    // Statistics
    SocialMediaStatsDTO getSocialMediaStats(Long profileId);
    Map<String, Long> getPlatformDistribution(Long profileId);
    List<String> getDistinctPlatforms(Long profileId);
    List<SocialMediaPlatformDTO> getPlatformInfo(Long profileId);

    // Validation & Utilities
    boolean existsByUrl(String url, Long profileId);
    boolean existsByPlatform(String platform, Long profileId);
    boolean isDisplayOrderAvailable(Long profileId, Integer displayOrder, Long excludeId);
    Integer getNextAvailableDisplayOrder(Long profileId);
    void reorderDisplayPositions(Long profileId);

    // Batch Operations
    List<SocialMediaResponseDTO> bulkCreateSocialMedia(List<SocialMediaDTO> socialMediaDTOs);
    void bulkUpdateVisibility(List<Long> ids, Boolean isVisible);
}
