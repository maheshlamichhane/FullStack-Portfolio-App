package com.portfolio.app.controller;

import com.portfolio.app.dto.*;
import com.portfolio.app.service.SocialMediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/social-media")
@RequiredArgsConstructor
@Tag(name = "Social Media", description = "Social media links management APIs")
@Slf4j
public class SocialMediaController {

    private final SocialMediaService socialMediaService;

    @PostMapping
    @Operation(summary = "Create a new social media link")
    public ResponseEntity<SocialMediaResponseDTO> createSocialMedia(
            @Valid @RequestBody SocialMediaDTO socialMediaDTO) {
        log.info("POST /api/v1/social-media - Creating new social media link: {}", socialMediaDTO.getPlatform());
        SocialMediaResponseDTO createdSocialMedia = socialMediaService.createSocialMedia(socialMediaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSocialMedia);
    }

    @PostMapping("/bulk")
    @Operation(summary = "Bulk create social media links")
    public ResponseEntity<List<SocialMediaResponseDTO>> bulkCreateSocialMedia(
            @Valid @RequestBody List<SocialMediaDTO> socialMediaDTOs) {
        log.info("POST /api/v1/social-media/bulk - Bulk creating {} social media links", socialMediaDTOs.size());
        List<SocialMediaResponseDTO> createdLinks = socialMediaService.bulkCreateSocialMedia(socialMediaDTOs);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLinks);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing social media link")
    public ResponseEntity<SocialMediaResponseDTO> updateSocialMedia(
            @PathVariable Long id,
            @Valid @RequestBody SocialMediaDTO socialMediaDTO) {
        log.info("PUT /api/v1/social-media/{} - Updating social media link", id);
        SocialMediaResponseDTO updatedSocialMedia = socialMediaService.updateSocialMedia(id, socialMediaDTO);
        return ResponseEntity.ok(updatedSocialMedia);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get social media link by ID")
    public ResponseEntity<SocialMediaResponseDTO> getSocialMedia(@PathVariable Long id) {
        log.debug("GET /api/v1/social-media/{} - Fetching social media link", id);
        SocialMediaResponseDTO socialMedia = socialMediaService.getSocialMediaById(id);
        return ResponseEntity.ok(socialMedia);
    }

    @GetMapping("/profile/{profileId}")
    @Operation(summary = "Get all social media links for a profile")
    public ResponseEntity<List<SocialMediaResponseDTO>> getSocialMediaByProfile(
            @PathVariable Long profileId,
            @RequestParam(required = false) String platform,
            @RequestParam(required = false) Boolean visibleOnly,
            @RequestParam(required = false) String search) {
        log.debug("GET /api/v1/social-media/profile/{} - Fetching social media links", profileId);

        List<SocialMediaResponseDTO> socialMediaLinks;

        if (search != null && !search.trim().isEmpty()) {
            socialMediaLinks = socialMediaService.searchSocialMedia(profileId, search);
        } else if (platform != null && !platform.trim().isEmpty()) {
            socialMediaLinks = socialMediaService.getSocialMediaByPlatform(profileId, platform);
        } else if (visibleOnly != null && visibleOnly) {
            socialMediaLinks = socialMediaService.getVisibleSocialMediaByProfile(profileId);
        } else {
            socialMediaLinks = socialMediaService.getAllSocialMediaByProfile(profileId);
        }

        return ResponseEntity.ok(socialMediaLinks);
    }

    @GetMapping("/profile/{profileId}/display-order")
    @Operation(summary = "Get social media links ordered by display order")
    public ResponseEntity<List<SocialMediaSummaryDTO>> getSocialMediaByDisplayOrder(@PathVariable Long profileId) {
        log.debug("GET /api/v1/social-media/profile/{}/display-order - Getting social media links by display order", profileId);
        List<SocialMediaSummaryDTO> socialMediaLinks = socialMediaService.getSocialMediaByDisplayOrder(profileId);
        return ResponseEntity.ok(socialMediaLinks);
    }

    @GetMapping("/profile/{profileId}/visible")
    @Operation(summary = "Get visible social media links")
    public ResponseEntity<List<SocialMediaResponseDTO>> getVisibleSocialMedia(@PathVariable Long profileId) {
        log.debug("GET /api/v1/social-media/profile/{}/visible - Getting visible social media links", profileId);
        List<SocialMediaResponseDTO> socialMediaLinks = socialMediaService.getVisibleSocialMediaByProfile(profileId);
        return ResponseEntity.ok(socialMediaLinks);
    }

    @GetMapping("/profile/{profileId}/url")
    @Operation(summary = "Get social media link by URL")
    public ResponseEntity<SocialMediaResponseDTO> getSocialMediaByUrl(
            @PathVariable Long profileId,
            @RequestParam String url) {
        log.debug("GET /api/v1/social-media/profile/{}/url?url={} - Getting social media by URL", profileId, url);
        SocialMediaResponseDTO socialMedia = socialMediaService.getSocialMediaByUrl(profileId, url);
        return ResponseEntity.ok(socialMedia);
    }

    @GetMapping("/stats/{profileId}")
    @Operation(summary = "Get social media statistics")
    public ResponseEntity<SocialMediaStatsDTO> getSocialMediaStats(@PathVariable Long profileId) {
        log.debug("GET /api/v1/social-media/stats/{} - Getting social media statistics", profileId);
        SocialMediaStatsDTO stats = socialMediaService.getSocialMediaStats(profileId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/platform-distribution/{profileId}")
    @Operation(summary = "Get platform distribution")
    public ResponseEntity<Map<String, Long>> getPlatformDistribution(@PathVariable Long profileId) {
        log.debug("GET /api/v1/social-media/platform-distribution/{} - Getting platform distribution", profileId);
        Map<String, Long> distribution = socialMediaService.getPlatformDistribution(profileId);
        return ResponseEntity.ok(distribution);
    }

    @GetMapping("/platforms/{profileId}")
    @Operation(summary = "Get distinct platforms")
    public ResponseEntity<List<String>> getDistinctPlatforms(@PathVariable Long profileId) {
        log.debug("GET /api/v1/social-media/platforms/{} - Getting distinct platforms", profileId);
        List<String> platforms = socialMediaService.getDistinctPlatforms(profileId);
        return ResponseEntity.ok(platforms);
    }

    @GetMapping("/platform-info/{profileId}")
    @Operation(summary = "Get platform information")
    public ResponseEntity<List<SocialMediaPlatformDTO>> getPlatformInfo(@PathVariable Long profileId) {
        log.debug("GET /api/v1/social-media/platform-info/{} - Getting platform information", profileId);
        List<SocialMediaPlatformDTO> platformInfo = socialMediaService.getPlatformInfo(profileId);
        return ResponseEntity.ok(platformInfo);
    }

    @PatchMapping("/{id}/toggle-visibility")
    @Operation(summary = "Toggle visibility of a social media link")
    public ResponseEntity<SocialMediaResponseDTO> toggleVisibility(@PathVariable Long id) {
        log.info("PATCH /api/v1/social-media/{}/toggle-visibility - Toggling visibility", id);
        SocialMediaResponseDTO socialMedia = socialMediaService.toggleVisibility(id);
        return ResponseEntity.ok(socialMedia);
    }

    @PatchMapping("/{id}/display-order")
    @Operation(summary = "Update display order of a social media link")
    public ResponseEntity<SocialMediaResponseDTO> updateDisplayOrder(
            @PathVariable Long id,
            @RequestParam Integer displayOrder) {
        log.info("PATCH /api/v1/social-media/{}/display-order?displayOrder={} - Updating display order",
                id, displayOrder);
        SocialMediaResponseDTO socialMedia = socialMediaService.updateDisplayOrder(id, displayOrder);
        return ResponseEntity.ok(socialMedia);
    }

    @PatchMapping("/bulk/display-orders")
    @Operation(summary = "Update multiple display orders")
    public ResponseEntity<Void> updateMultipleDisplayOrders(
            @RequestBody Map<Long, Integer> displayOrderUpdates) {
        log.info("PATCH /api/v1/social-media/bulk/display-orders - Updating multiple display orders");
        socialMediaService.updateMultipleDisplayOrders(displayOrderUpdates);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/bulk/visibility")
    @Operation(summary = "Bulk update visibility")
    public ResponseEntity<Void> bulkUpdateVisibility(
            @RequestBody List<Long> ids,
            @RequestParam Boolean isVisible) {
        log.info("PATCH /api/v1/social-media/bulk/visibility - Bulk updating visibility for {} links", ids.size());
        socialMediaService.bulkUpdateVisibility(ids, isVisible);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reorder/{profileId}")
    @Operation(summary = "Reorganize display positions")
    public ResponseEntity<Map<String, Object>> reorderDisplayPositions(@PathVariable Long profileId) {
        log.info("POST /api/v1/social-media/reorder/{} - Reordering display positions", profileId);
        socialMediaService.reorderDisplayPositions(profileId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Display positions reorganized successfully");
        response.put("profileId", profileId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-url")
    @Operation(summary = "Check if URL exists for profile")
    public ResponseEntity<Map<String, Boolean>> checkUrlExists(
            @RequestParam String url,
            @RequestParam Long profileId) {
        log.debug("GET /api/v1/social-media/check-url?url={}&profileId={} - Checking URL existence", url, profileId);
        boolean exists = socialMediaService.existsByUrl(url, profileId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        response.put("available", !exists);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-platform")
    @Operation(summary = "Check if platform exists for profile")
    public ResponseEntity<Map<String, Boolean>> checkPlatformExists(
            @RequestParam String platform,
            @RequestParam Long profileId) {
        log.debug("GET /api/v1/social-media/check-platform?platform={}&profileId={} - Checking platform existence",
                platform, profileId);
        boolean exists = socialMediaService.existsByPlatform(platform, profileId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/next-display-order/{profileId}")
    @Operation(summary = "Get next available display order")
    public ResponseEntity<Map<String, Object>> getNextDisplayOrder(@PathVariable Long profileId) {
        log.debug("GET /api/v1/social-media/next-display-order/{} - Getting next display order", profileId);
        Integer nextOrder = socialMediaService.getNextAvailableDisplayOrder(profileId);

        Map<String, Object> response = new HashMap<>();
        response.put("profileId", profileId);
        response.put("nextDisplayOrder", nextOrder);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a social media link")
    public ResponseEntity<Void> deleteSocialMedia(@PathVariable Long id) {
        log.info("DELETE /api/v1/social-media/{} - Deleting social media link", id);
        socialMediaService.deleteSocialMedia(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/profile/{profileId}")
    @Operation(summary = "Delete all social media links for a profile")
    public ResponseEntity<Void> deleteAllSocialMediaByProfile(@PathVariable Long profileId) {
        log.info("DELETE /api/v1/social-media/profile/{} - Deleting all social media links", profileId);
        socialMediaService.deleteAllSocialMediaByProfile(profileId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/profile/{profileId}/platform/{platform}")
    @Operation(summary = "Delete social media links by platform")
    public ResponseEntity<Map<String, Object>> deleteSocialMediaByPlatform(
            @PathVariable Long profileId,
            @PathVariable String platform) {
        log.info("DELETE /api/v1/social-media/profile/{}/platform/{} - Deleting social media by platform",
                profileId, platform);
        socialMediaService.deleteSocialMediaByPlatform(profileId, platform);

        Map<String, Object> response = new HashMap<>();
        response.put("message", String.format("Social media links for platform '%s' deleted successfully", platform));
        response.put("profileId", profileId);
        response.put("platform", platform);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/export/{profileId}")
    @Operation(summary = "Export social media links")
    public ResponseEntity<Map<String, Object>> exportSocialMedia(
            @PathVariable Long profileId,
            @RequestParam(defaultValue = "json") String format) {
        log.debug("GET /api/v1/social-media/export/{}?format={} - Exporting social media links", profileId, format);

        List<SocialMediaResponseDTO> socialMediaLinks = socialMediaService.getAllSocialMediaByProfile(profileId);
        SocialMediaStatsDTO stats = socialMediaService.getSocialMediaStats(profileId);
        List<SocialMediaPlatformDTO> platformInfo = socialMediaService.getPlatformInfo(profileId);

        Map<String, Object> exportData = new HashMap<>();
        exportData.put("generatedAt", java.time.LocalDateTime.now().toString());
        exportData.put("profileId", profileId);
        exportData.put("totalLinks", socialMediaLinks.size());
        exportData.put("socialMediaLinks", socialMediaLinks);
        exportData.put("statistics", stats);
        exportData.put("platformInfo", platformInfo);
        exportData.put("format", format);

        return ResponseEntity.ok(exportData);
    }

    @GetMapping("/share-buttons/{profileId}")
    @Operation(summary = "Get social media share buttons configuration")
    public ResponseEntity<Map<String, Object>> getShareButtonsConfig(@PathVariable Long profileId) {
        log.debug("GET /api/v1/social-media/share-buttons/{} - Getting share buttons configuration", profileId);

        List<SocialMediaResponseDTO> visibleLinks = socialMediaService.getVisibleSocialMediaByProfile(profileId);
        SocialMediaStatsDTO stats = socialMediaService.getSocialMediaStats(profileId);

        Map<String, Object> config = new HashMap<>();
        config.put("profileId", profileId);
        config.put("totalVisibleLinks", visibleLinks.size());
        config.put("socialMediaLinks", visibleLinks);
        config.put("statistics", stats);

        // Generate HTML/CSS/JS snippets for share buttons
        if (!visibleLinks.isEmpty()) {
            config.put("htmlSnippet", generateHtmlSnippet(visibleLinks));
            config.put("cssSnippet", generateCssSnippet());
            config.put("jsSnippet", generateJsSnippet());
        }

        return ResponseEntity.ok(config);
    }

    private String generateHtmlSnippet(List<SocialMediaResponseDTO> links) {
        StringBuilder html = new StringBuilder();
        html.append("<div class=\"social-media-links\">\n");

        for (SocialMediaResponseDTO link : links) {
            html.append(String.format(
                    "  <a href=\"%s\" target=\"_blank\" rel=\"noopener noreferrer\" " +
                            "class=\"social-link %s\" title=\"%s\">\n",
                    link.getUrl(),
                    link.getPlatform().toLowerCase().replace(" ", "-"),
                    link.getPlatform()
            ));
            html.append(String.format(
                    "    <i class=\"%s\"></i>\n",
                    link.getIconClass() != null ? link.getIconClass() : link.getPlatformIcon()
            ));
            html.append("  </a>\n");
        }

        html.append("</div>");
        return html.toString();
    }

    private String generateCssSnippet() {
        return """
            .social-media-links {
                display: flex;
                flex-wrap: wrap;
                gap: 15px;
                justify-content: center;
                margin: 20px 0;
            }
            
            .social-link {
                display: inline-flex;
                align-items: center;
                justify-content: center;
                width: 50px;
                height: 50px;
                border-radius: 50%;
                background-color: #f0f0f0;
                color: #333;
                font-size: 20px;
                text-decoration: none;
                transition: all 0.3s ease;
            }
            
            .social-link:hover {
                transform: translateY(-3px);
                box-shadow: 0 5px 15px rgba(0,0,0,0.1);
            }
            
            .social-link.github:hover { background-color: #333; color: white; }
            .social-link.linkedin:hover { background-color: #0077b5; color: white; }
            .social-link.twitter:hover { background-color: #1da1f2; color: white; }
            .social-link.facebook:hover { background-color: #1877f2; color: white; }
            .social-link.instagram:hover { background-color: #e4405f; color: white; }
            .social-link.youtube:hover { background-color: #ff0000; color: white; }
            """;
    }

    private String generateJsSnippet() {
        return """
            // Social media links interaction
            document.addEventListener('DOMContentLoaded', function() {
                const socialLinks = document.querySelectorAll('.social-link');
                
                socialLinks.forEach(link => {
                    link.addEventListener('click', function(e) {
                        // Track social media clicks (if you have analytics)
                        console.log('Social media link clicked:', this.href);
                    });
                });
            });
            """;
    }
}
