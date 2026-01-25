package com.portfolio.app.service;


import com.portfolio.app.dao.ProfileRepository;
import com.portfolio.app.dao.SocialMediaRepository;
import com.portfolio.app.dto.*;
import com.portfolio.app.entity.SocialMedia;
import com.portfolio.app.entity.Profile;
import com.portfolio.app.exception.ResourceNotFoundException;
import com.portfolio.app.exception.BusinessRuleException;
import com.portfolio.app.mapper.SocialMediaMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocialMediaServiceImpl implements SocialMediaService {

    private final SocialMediaRepository socialMediaRepository;
    private final ProfileRepository profileRepository;
    private final SocialMediaMapper socialMediaMapper;

    private static final Map<String, String> PLATFORM_EXAMPLES = Map.ofEntries(
            Map.entry("GitHub", "https://github.com/username"),
            Map.entry("LinkedIn", "https://linkedin.com/in/username"),
            Map.entry("Twitter", "https://twitter.com/username"),
            Map.entry("Facebook", "https://facebook.com/username"),
            Map.entry("Instagram", "https://instagram.com/username"),
            Map.entry("YouTube", "https://youtube.com/username"),
            Map.entry("Medium", "https://medium.com/@username"),
            Map.entry("Stack Overflow", "https://stackoverflow.com/users/userid/username"),
            Map.entry("GitLab", "https://gitlab.com/username"),
            Map.entry("Bitbucket", "https://bitbucket.org/username"),
            Map.entry("Behance", "https://behance.net/username"),
            Map.entry("Dribbble", "https://dribbble.com/username"),
            Map.entry("CodePen", "https://codepen.io/username"),
            Map.entry("Dev.to", "https://dev.to/username"),
            Map.entry("HackerRank", "https://hackerrank.com/username"),
            Map.entry("LeetCode", "https://leetcode.com/username"),
            Map.entry("Kaggle", "https://kaggle.com/username"),
            Map.entry("Reddit", "https://reddit.com/user/username"),
            Map.entry("Twitch", "https://twitch.tv/username"),
            Map.entry("Discord", "https://discord.gg/invitecode"),
            Map.entry("Slack", "https://slack.com"),
            Map.entry("Telegram", "https://t.me/username"),
            Map.entry("WhatsApp", "https://wa.me/phonenumber"),
            Map.entry("Skype", "skype:username?call"),
            Map.entry("Website", "https://yourwebsite.com"),
            Map.entry("Blog", "https://yourblog.com"),
            Map.entry("Portfolio", "https://yourportfolio.com"),
            Map.entry("Email", "mailto:email@example.com"),
            Map.entry("Phone", "tel:+1234567890")
    );

    @Override
    @Transactional
    public SocialMediaResponseDTO createSocialMedia(SocialMediaDTO socialMediaDTO) {
        log.info("Creating new social media link for profile {}: {}",
                socialMediaDTO.getProfileId(), socialMediaDTO.getPlatform());

        // Validate profile exists
        Profile profile = profileRepository.findById(socialMediaDTO.getProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "id", socialMediaDTO.getProfileId()));

        // Validate URL uniqueness
        if (socialMediaRepository.existsByUrlAndProfileId(socialMediaDTO.getUrl(), socialMediaDTO.getProfileId())) {
            throw new BusinessRuleException("URL '" + socialMediaDTO.getUrl() + "' already exists for this profile");
        }

        // Validate platform uniqueness (optional, some profiles might have multiple accounts on same platform)
        if (socialMediaRepository.existsByPlatformAndProfileId(socialMediaDTO.getPlatform(), socialMediaDTO.getProfileId())) {
            log.warn("Platform '{}' already exists for profile {}, but allowing multiple accounts",
                    socialMediaDTO.getPlatform(), socialMediaDTO.getProfileId());
        }

        // Check if display order is available
        if (!isDisplayOrderAvailable(socialMediaDTO.getProfileId(), socialMediaDTO.getDisplayOrder(), null)) {
            throw new BusinessRuleException("Display order " + socialMediaDTO.getDisplayOrder() + " is already taken");
        }

        // Extract username from URL if not provided
        if ((socialMediaDTO.getUsername() == null || socialMediaDTO.getUsername().trim().isEmpty()) &&
                socialMediaDTO.getUrl() != null) {
            String username = extractUsernameFromUrl(socialMediaDTO.getUrl(), socialMediaDTO.getPlatform());
            socialMediaDTO.setUsername(username);
        }

        SocialMedia socialMedia = socialMediaMapper.toEntity(socialMediaDTO);
        socialMedia.setProfile(profile);

        SocialMedia savedSocialMedia = socialMediaRepository.save(socialMedia);
        log.info("Social media link created successfully with ID: {}", savedSocialMedia.getId());

        return socialMediaMapper.toResponseDto(savedSocialMedia);
    }

    @Override
    @Transactional(readOnly = true)
    public SocialMediaResponseDTO getSocialMediaById(Long id) {
        log.debug("Fetching social media by ID: {}", id);

        SocialMedia socialMedia = socialMediaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SocialMedia", "id", id));

        return socialMediaMapper.toResponseDto(socialMedia);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SocialMediaResponseDTO> getAllSocialMediaByProfile(Long profileId) {
        log.debug("Fetching all social media links for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return socialMediaRepository.findByProfileIdOrderByDisplayOrderAsc(profileId)
                .stream()
                .map(socialMediaMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SocialMediaResponseDTO> getVisibleSocialMediaByProfile(Long profileId) {
        log.debug("Fetching visible social media links for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return socialMediaRepository.findByProfileIdAndIsVisibleTrue(profileId)
                .stream()
                .map(socialMediaMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SocialMediaResponseDTO> getSocialMediaByPlatform(Long profileId, String platform) {
        log.debug("Fetching social media links for platform '{}' and profile ID: {}", platform, profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return socialMediaRepository.findByProfileIdAndPlatform(profileId, platform)
                .stream()
                .map(socialMediaMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SocialMediaSummaryDTO> getSocialMediaByDisplayOrder(Long profileId) {
        log.debug("Fetching social media links by display order for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return socialMediaRepository.findByProfileIdOrderByDisplayOrderAsc(profileId)
                .stream()
                .map(socialMediaMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SocialMediaResponseDTO getSocialMediaByUrl(Long profileId, String url) {
        log.debug("Fetching social media by URL '{}' for profile ID: {}", url, profileId);

        SocialMedia socialMedia = socialMediaRepository.findByUrlAndProfileId(url, profileId)
                .orElseThrow(() -> new ResourceNotFoundException("SocialMedia", "url", url));

        return socialMediaMapper.toResponseDto(socialMedia);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SocialMediaResponseDTO> searchSocialMedia(Long profileId, String query) {
        log.debug("Searching social media with query '{}' for profile ID: {}", query, profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        if (query == null || query.trim().isEmpty()) {
            return getAllSocialMediaByProfile(profileId);
        }

        return socialMediaRepository.searchSocialMedia(profileId, query.trim())
                .stream()
                .map(socialMediaMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SocialMediaResponseDTO updateSocialMedia(Long id, SocialMediaDTO socialMediaDTO) {
        log.info("Updating social media with ID: {}", id);

        SocialMedia existingSocialMedia = socialMediaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SocialMedia", "id", id));

        // Check if URL is being changed and if it's available
        if (!existingSocialMedia.getUrl().equals(socialMediaDTO.getUrl())) {
            if (socialMediaRepository.existsByUrlAndProfileId(socialMediaDTO.getUrl(), socialMediaDTO.getProfileId())) {
                throw new BusinessRuleException("URL '" + socialMediaDTO.getUrl() + "' already exists for this profile");
            }
        }

        // Check if display order is being changed and if it's available
        if (!existingSocialMedia.getDisplayOrder().equals(socialMediaDTO.getDisplayOrder())) {
            if (!isDisplayOrderAvailable(socialMediaDTO.getProfileId(), socialMediaDTO.getDisplayOrder(), id)) {
                throw new BusinessRuleException("Display order " + socialMediaDTO.getDisplayOrder() + " is already taken");
            }
        }

        // Extract username from URL if not provided
        if ((socialMediaDTO.getUsername() == null || socialMediaDTO.getUsername().trim().isEmpty()) &&
                socialMediaDTO.getUrl() != null && !socialMediaDTO.getUrl().equals(existingSocialMedia.getUrl())) {
            String username = extractUsernameFromUrl(socialMediaDTO.getUrl(), socialMediaDTO.getPlatform());
            socialMediaDTO.setUsername(username);
        }

        // Update profile if changed
        if (!existingSocialMedia.getProfile().getId().equals(socialMediaDTO.getProfileId())) {
            Profile profile = profileRepository.findById(socialMediaDTO.getProfileId())
                    .orElseThrow(() -> new ResourceNotFoundException("Profile", "id", socialMediaDTO.getProfileId()));
            existingSocialMedia.setProfile(profile);
        }

        socialMediaMapper.updateEntityFromDto(socialMediaDTO, existingSocialMedia);

        SocialMedia updatedSocialMedia = socialMediaRepository.save(existingSocialMedia);
        log.info("Social media updated successfully: {}", id);

        return socialMediaMapper.toResponseDto(updatedSocialMedia);
    }

    @Override
    @Transactional
    public SocialMediaResponseDTO toggleVisibility(Long id) {
        log.info("Toggling visibility for social media ID: {}", id);

        SocialMedia socialMedia = socialMediaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SocialMedia", "id", id));

        socialMedia.setIsVisible(!Boolean.TRUE.equals(socialMedia.getIsVisible()));

        SocialMedia updatedSocialMedia = socialMediaRepository.save(socialMedia);
        log.info("Social media visibility toggled: {} is now {}", id, updatedSocialMedia.getIsVisible());

        return socialMediaMapper.toResponseDto(updatedSocialMedia);
    }

    @Override
    @Transactional
    public SocialMediaResponseDTO updateDisplayOrder(Long id, Integer displayOrder) {
        log.info("Updating display order for social media ID: {} to {}", id, displayOrder);

        SocialMedia socialMedia = socialMediaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SocialMedia", "id", id));

        // Check if display order is available
        if (!isDisplayOrderAvailable(socialMedia.getProfile().getId(), displayOrder, id)) {
            throw new BusinessRuleException("Display order " + displayOrder + " is already taken");
        }

        socialMedia.setDisplayOrder(displayOrder);
        SocialMedia updatedSocialMedia = socialMediaRepository.save(socialMedia);

        return socialMediaMapper.toResponseDto(updatedSocialMedia);
    }

    @Override
    @Transactional
    public void updateMultipleDisplayOrders(Map<Long, Integer> displayOrderUpdates) {
        log.info("Updating display orders for {} social media links", displayOrderUpdates.size());

        List<SocialMedia> socialMediaToUpdate = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : displayOrderUpdates.entrySet()) {
            SocialMedia socialMedia = socialMediaRepository.findById(entry.getKey())
                    .orElseThrow(() -> new ResourceNotFoundException("SocialMedia", "id", entry.getKey()));

            socialMedia.setDisplayOrder(entry.getValue());
            socialMediaToUpdate.add(socialMedia);
        }

        // Check for duplicate display orders within the batch
        Set<Integer> usedDisplayOrders = new HashSet<>();
        for (SocialMedia sm : socialMediaToUpdate) {
            if (!usedDisplayOrders.add(sm.getDisplayOrder())) {
                throw new BusinessRuleException("Duplicate display order found: " + sm.getDisplayOrder());
            }
        }

        socialMediaRepository.saveAll(socialMediaToUpdate);
    }

    @Override
    @Transactional
    public void deleteSocialMedia(Long id) {
        log.info("Deleting social media with ID: {}", id);

        if (!socialMediaRepository.existsById(id)) {
            throw new ResourceNotFoundException("SocialMedia", "id", id);
        }

        socialMediaRepository.deleteById(id);
        log.info("Social media deleted: {}", id);
    }

    @Override
    @Transactional
    public void deleteAllSocialMediaByProfile(Long profileId) {
        log.info("Deleting all social media links for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        socialMediaRepository.deleteByProfileId(profileId);
    }

    @Override
    @Transactional
    public void deleteSocialMediaByPlatform(Long profileId, String platform) {
        log.info("Deleting social media links for platform '{}' and profile ID: {}", platform, profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        List<SocialMedia> socialMediaList = socialMediaRepository.findByProfileIdAndPlatform(profileId, platform);
        socialMediaRepository.deleteAll(socialMediaList);

        log.info("Deleted {} social media links for platform '{}'", socialMediaList.size(), platform);
    }

    @Override
    @Transactional(readOnly = true)
    public SocialMediaStatsDTO getSocialMediaStats(Long profileId) {
        log.debug("Getting social media statistics for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        List<SocialMedia> socialMediaList = socialMediaRepository.findByProfileId(profileId);

        if (socialMediaList.isEmpty()) {
            return new SocialMediaStatsDTO(0L, 0L, 0L, Map.of(), 0, "None");
        }

        // Calculate statistics
        long totalSocialLinks = socialMediaList.size();
        long visibleLinks = socialMediaList.stream()
                .filter(sm -> Boolean.TRUE.equals(sm.getIsVisible()))
                .count();
        long hiddenLinks = totalSocialLinks - visibleLinks;

        // Platform distribution
        Map<String, Long> platformDistribution = socialMediaList.stream()
                .collect(Collectors.groupingBy(
                        SocialMedia::getPlatform,
                        Collectors.counting()
                ));

        // Find most used platform
        Map.Entry<String, Long> mostUsed = platformDistribution.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        return new SocialMediaStatsDTO(
                totalSocialLinks,
                visibleLinks,
                hiddenLinks,
                platformDistribution,
                mostUsed != null ? mostUsed.getValue().intValue() : 0,
                mostUsed != null ? mostUsed.getKey() : "None"
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getPlatformDistribution(Long profileId) {
        List<Object[]> results = socialMediaRepository.countSocialMediaByPlatform(profileId);

        Map<String, Long> distribution = new HashMap<>();
        for (Object[] result : results) {
            distribution.put((String) result[0], (Long) result[1]);
        }

        return distribution;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getDistinctPlatforms(Long profileId) {
        return socialMediaRepository.findDistinctPlatforms(profileId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SocialMediaPlatformDTO> getPlatformInfo(Long profileId) {
        List<String> platforms = socialMediaRepository.findDistinctPlatforms(profileId);

        return platforms.stream()
                .map(platform -> {
                    long count = socialMediaRepository.countByProfileIdAndPlatform(profileId, platform);
                    String defaultIcon = socialMediaMapper.getPlatformIcon(platform);
                    String exampleUrl = PLATFORM_EXAMPLES.getOrDefault(platform, "https://example.com");

                    return new SocialMediaPlatformDTO(platform, count, defaultIcon, exampleUrl);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUrl(String url, Long profileId) {
        return socialMediaRepository.existsByUrlAndProfileId(url, profileId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByPlatform(String platform, Long profileId) {
        return socialMediaRepository.existsByPlatformAndProfileId(platform, profileId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isDisplayOrderAvailable(Long profileId, Integer displayOrder, Long excludeId) {
        Optional<SocialMedia> existing = socialMediaRepository.findByProfileIdAndDisplayOrder(profileId, displayOrder);

        if (existing.isEmpty()) {
            return true;
        }

        return excludeId != null && existing.get().getId().equals(excludeId);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getNextAvailableDisplayOrder(Long profileId) {
        return socialMediaRepository.getNextDisplayOrder(profileId);
    }

    @Override
    @Transactional
    public void reorderDisplayPositions(Long profileId) {
        log.info("Reordering display positions for profile ID: {}", profileId);

        List<SocialMedia> socialMediaList = socialMediaRepository.findByProfileIdOrderByDisplayOrderAsc(profileId);

        int order = 1;
        for (SocialMedia socialMedia : socialMediaList) {
            socialMedia.setDisplayOrder(order++);
        }

        socialMediaRepository.saveAll(socialMediaList);
        log.info("Reordered {} social media links", socialMediaList.size());
    }

    @Override
    @Transactional
    public List<SocialMediaResponseDTO> bulkCreateSocialMedia(List<SocialMediaDTO> socialMediaDTOs) {
        log.info("Bulk creating {} social media links", socialMediaDTOs.size());

        List<SocialMediaResponseDTO> createdLinks = new ArrayList<>();

        for (SocialMediaDTO dto : socialMediaDTOs) {
            try {
                SocialMediaResponseDTO created = createSocialMedia(dto);
                createdLinks.add(created);
            } catch (Exception e) {
                log.error("Failed to create social media link: {}", dto.getPlatform(), e);
            }
        }

        return createdLinks;
    }

    @Override
    @Transactional
    public void bulkUpdateVisibility(List<Long> ids, Boolean isVisible) {
        log.info("Bulk updating visibility for {} social media links to {}", ids.size(), isVisible);

        List<SocialMedia> socialMediaList = socialMediaRepository.findAllById(ids);
        socialMediaList.forEach(sm -> sm.setIsVisible(isVisible));
        socialMediaRepository.saveAll(socialMediaList);
    }

    private String extractUsernameFromUrl(String url, String platform) {
        if (url == null || platform == null) return "";

        String lowercasePlatform = platform.toLowerCase();
        String lowercaseUrl = url.toLowerCase();

        try {
            if (lowercasePlatform.contains("github") && lowercaseUrl.contains("github.com/")) {
                return extractPathSegment(url, "github.com/");
            } else if (lowercasePlatform.contains("linkedin") && lowercaseUrl.contains("linkedin.com/in/")) {
                return extractPathSegment(url, "linkedin.com/in/");
            } else if (lowercasePlatform.contains("twitter") && lowercaseUrl.contains("twitter.com/")) {
                return extractPathSegment(url, "twitter.com/");
            } else if (lowercasePlatform.contains("instagram") && lowercaseUrl.contains("instagram.com/")) {
                return extractPathSegment(url, "instagram.com/");
            } else if (lowercasePlatform.contains("youtube") && lowercaseUrl.contains("youtube.com/")) {
                return extractPathSegment(url, "youtube.com/");
            } else if (lowercasePlatform.contains("medium") && lowercaseUrl.contains("medium.com/@")) {
                return extractPathSegment(url, "medium.com/@");
            } else if (lowercasePlatform.contains("stackoverflow") && lowercaseUrl.contains("stackoverflow.com/users/")) {
                // Extract after /users/
                String[] parts = url.split("stackoverflow.com/users/");
                if (parts.length > 1) {
                    String[] subparts = parts[1].split("/");
                    return subparts.length > 0 ? subparts[0] : parts[1];
                }
            }
        } catch (Exception e) {
            log.warn("Failed to extract username from URL: {}", url, e);
        }

        // Generic extraction: try to get the last part of the path
        try {
            java.net.URI uri = new java.net.URI(url);
            String path = uri.getPath();
            if (path != null && !path.isEmpty()) {
                String[] segments = path.split("/");
                for (int i = segments.length - 1; i >= 0; i--) {
                    if (!segments[i].isEmpty() && !segments[i].matches("\\d+")) {
                        return segments[i];
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Failed to parse URL for username extraction: {}", url, e);
        }

        return "";
    }

    private String extractPathSegment(String url, String pattern) {
        int index = url.toLowerCase().indexOf(pattern);
        if (index != -1) {
            String remaining = url.substring(index + pattern.length());
            // Remove query parameters and fragments
            String[] parts = remaining.split("[?#/]");
            return parts.length > 0 ? parts[0] : "";
        }
        return "";
    }
}
