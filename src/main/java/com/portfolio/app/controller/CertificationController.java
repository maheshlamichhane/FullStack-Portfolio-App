package com.portfolio.app.controller;


import com.portfolio.app.dto.*;
import com.portfolio.app.service.CertificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/certifications")
@RequiredArgsConstructor
@Tag(name = "Certification", description = "Certification management APIs")
@Slf4j
public class CertificationController {

    private final CertificationService certificationService;

    @PostMapping
    @Operation(summary = "Create a new certification")
    public ResponseEntity<CertificationResponseDTO> createCertification(
            @Valid @RequestBody CertificationDTO certificationDTO) {
        log.info("POST /api/v1/certifications - Creating new certification: {}", certificationDTO.getName());
        CertificationResponseDTO createdCertification = certificationService.createCertification(certificationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCertification);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing certification")
    public ResponseEntity<CertificationResponseDTO> updateCertification(
            @PathVariable Long id,
            @Valid @RequestBody CertificationDTO certificationDTO) {
        log.info("PUT /api/v1/certifications/{} - Updating certification", id);
        CertificationResponseDTO updatedCertification = certificationService.updateCertification(id, certificationDTO);
        return ResponseEntity.ok(updatedCertification);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get certification by ID")
    public ResponseEntity<CertificationResponseDTO> getCertification(@PathVariable Long id) {
        log.debug("GET /api/v1/certifications/{} - Fetching certification", id);
        CertificationResponseDTO certification = certificationService.getCertificationById(id);
        return ResponseEntity.ok(certification);
    }

    @GetMapping("/profile/{profileId}")
    @Operation(summary = "Get all certifications for a profile")
    public ResponseEntity<List<CertificationResponseDTO>> getCertificationsByProfile(
            @PathVariable Long profileId,
            @RequestParam(required = false) String organization,
            @RequestParam(required = false) Boolean verified,
            @RequestParam(required = false) Boolean expired,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String search) {
        log.debug("GET /api/v1/certifications/profile/{} - Fetching certifications", profileId);

        List<CertificationResponseDTO> certifications;

        if (search != null && !search.trim().isEmpty()) {
            certifications = certificationService.searchCertifications(profileId, search);
        } else if (organization != null && !organization.trim().isEmpty()) {
            certifications = certificationService.getCertificationsByOrganization(profileId, organization);
        } else if (verified != null && verified) {
            certifications = certificationService.getVerifiedCertifications(profileId);
        } else if (expired != null && expired) {
            certifications = certificationService.getExpiredCertifications(profileId);
        } else if (active != null && active) {
            certifications = certificationService.getActiveCertifications(profileId);
        } else {
            certifications = certificationService.getAllCertificationsByProfile(profileId);
        }

        return ResponseEntity.ok(certifications);
    }

    @GetMapping("/profile/{profileId}/paginated")
    @Operation(summary = "Get certifications for a profile with pagination")
    public ResponseEntity<Page<CertificationResponseDTO>> getCertificationsByProfilePaginated(
            @PathVariable Long profileId,
            @PageableDefault(size = 20, sort = "issueDate", direction = Sort.Direction.DESC)
            Pageable pageable) {
        log.debug("GET /api/v1/certifications/profile/{}/paginated - Fetching certifications with pagination", profileId);
        Page<CertificationResponseDTO> certifications = certificationService.getCertificationsByProfile(profileId, pageable);
        return ResponseEntity.ok(certifications);
    }

    @GetMapping("/profile/{profileId}/expiring-soon")
    @Operation(summary = "Get certifications expiring soon")
    public ResponseEntity<List<CertificationResponseDTO>> getCertificationsExpiringSoon(
            @PathVariable Long profileId,
            @RequestParam(defaultValue = "30") int days) {
        log.debug("GET /api/v1/certifications/profile/{}/expiring-soon?days={} - Getting certifications expiring soon",
                profileId, days);
        List<CertificationResponseDTO> certifications = certificationService.getCertificationsExpiringSoon(profileId, days);
        return ResponseEntity.ok(certifications);
    }

    @GetMapping("/profile/{profileId}/upcoming-expirations")
    @Operation(summary = "Get upcoming expirations")
    public ResponseEntity<List<CertificationSummaryDTO>> getUpcomingExpirations(
            @PathVariable Long profileId,
            @RequestParam(defaultValue = "10") int limit) {
        log.debug("GET /api/v1/certifications/profile/{}/upcoming-expirations?limit={} - Getting upcoming expirations",
                profileId, limit);
        List<CertificationSummaryDTO> certifications = certificationService.getUpcomingExpirations(profileId, limit);
        return ResponseEntity.ok(certifications);
    }

    @GetMapping("/stats/{profileId}")
    @Operation(summary = "Get certification statistics")
    public ResponseEntity<CertificationStatsDTO> getCertificationStats(@PathVariable Long profileId) {
        log.debug("GET /api/v1/certifications/stats/{} - Getting certification statistics", profileId);
        CertificationStatsDTO stats = certificationService.getCertificationStats(profileId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/organizations/{profileId}")
    @Operation(summary = "Get distinct issuing organizations")
    public ResponseEntity<List<String>> getDistinctOrganizations(@PathVariable Long profileId) {
        log.debug("GET /api/v1/certifications/organizations/{} - Getting distinct organizations", profileId);
        List<String> organizations = certificationService.getDistinctOrganizations(profileId);
        return ResponseEntity.ok(organizations);
    }

    @GetMapping("/organization-distribution/{profileId}")
    @Operation(summary = "Get organization distribution")
    public ResponseEntity<Map<String, Long>> getOrganizationDistribution(@PathVariable Long profileId) {
        log.debug("GET /api/v1/certifications/organization-distribution/{} - Getting organization distribution", profileId);
        Map<String, Long> distribution = certificationService.getOrganizationDistribution(profileId);
        return ResponseEntity.ok(distribution);
    }

    @PatchMapping("/{id}/verify")
    @Operation(summary = "Verify a certification")
    public ResponseEntity<CertificationResponseDTO> verifyCertification(@PathVariable Long id) {
        log.info("PATCH /api/v1/certifications/{}/verify - Verifying certification", id);
        CertificationResponseDTO certification = certificationService.verifyCertification(id);
        return ResponseEntity.ok(certification);
    }

    @PatchMapping("/{id}/unverify")
    @Operation(summary = "Unverify a certification")
    public ResponseEntity<CertificationResponseDTO> unverifyCertification(@PathVariable Long id) {
        log.info("PATCH /api/v1/certifications/{}/unverify - Unverifying certification", id);
        CertificationResponseDTO certification = certificationService.unverifyCertification(id);
        return ResponseEntity.ok(certification);
    }

    @PatchMapping("/{id}/renew")
    @Operation(summary = "Renew a certification")
    public ResponseEntity<CertificationResponseDTO> renewCertification(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newExpirationDate) {
        log.info("PATCH /api/v1/certifications/{}/renew - Renewing certification", id);
        CertificationResponseDTO certification = certificationService.renewCertification(id, newExpirationDate);
        return ResponseEntity.ok(certification);
    }

    @GetMapping("/check-credential")
    @Operation(summary = "Check if credential ID is available")
    public ResponseEntity<Map<String, Boolean>> checkCredentialId(
            @RequestParam String credentialId,
            @RequestParam Long profileId,
            @RequestParam(required = false) Long excludeId) {
        log.debug("GET /api/v1/certifications/check-credential - Checking credential ID availability");
        boolean available = certificationService.isCredentialIdAvailable(credentialId, profileId, excludeId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("available", available);
        response.put("exists", !available);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/expired")
    @Operation(summary = "Check if certification is expired")
    public ResponseEntity<Map<String, Boolean>> isCertificationExpired(@PathVariable Long id) {
        log.debug("GET /api/v1/certifications/{}/expired - Checking if certification is expired", id);
        boolean isExpired = certificationService.isCertificationExpired(id);

        Map<String, Boolean> response = new HashMap<>();
        response.put("isExpired", isExpired);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/days-until-expiration")
    @Operation(summary = "Get days until expiration")
    public ResponseEntity<Map<String, Object>> getDaysUntilExpiration(@PathVariable Long id) {
        log.debug("GET /api/v1/certifications/{}/days-until-expiration - Getting days until expiration", id);
        Integer days = certificationService.getDaysUntilExpiration(id);

        Map<String, Object> response = new HashMap<>();
        response.put("certificationId", id);
        response.put("daysUntilExpiration", days);

        if (days != null) {
            response.put("status", days > 0 ? "ACTIVE" : "EXPIRED");
            if (days > 0 && days <= 30) {
                response.put("warning", "Certification expires soon");
            }
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/notifications/{profileId}")
    @Operation(summary = "Get certification expiration notifications")
    public ResponseEntity<List<CertificationResponseDTO>> getExpirationNotifications(
            @PathVariable Long profileId,
            @RequestParam(defaultValue = "30") int daysThreshold) {
        log.debug("GET /api/v1/certifications/notifications/{} - Getting expiration notifications", profileId);
        List<CertificationResponseDTO> notifications =
                certificationService.getExpiringCertificationsNotifications(profileId, daysThreshold);
        return ResponseEntity.ok(notifications);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a certification")
    public ResponseEntity<Void> deleteCertification(@PathVariable Long id) {
        log.info("DELETE /api/v1/certifications/{} - Deleting certification", id);
        certificationService.deleteCertification(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/profile/{profileId}")
    @Operation(summary = "Delete all certifications for a profile")
    public ResponseEntity<Void> deleteAllCertificationsByProfile(@PathVariable Long profileId) {
        log.info("DELETE /api/v1/certifications/profile/{} - Deleting all certifications", profileId);
        certificationService.deleteAllCertificationsByProfile(profileId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/profile/{profileId}/expired")
    @Operation(summary = "Delete expired certifications")
    public ResponseEntity<Map<String, Object>> deleteExpiredCertifications(@PathVariable Long profileId) {
        log.info("DELETE /api/v1/certifications/profile/{}/expired - Deleting expired certifications", profileId);
        certificationService.deleteExpiredCertifications(profileId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Expired certifications deleted successfully");
        response.put("profileId", profileId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/timeline/{profileId}")
    @Operation(summary = "Get certification timeline")
    public ResponseEntity<List<Map<String, Object>>> getCertificationTimeline(@PathVariable Long profileId) {
        log.debug("GET /api/v1/certifications/timeline/{} - Getting certification timeline", profileId);

        List<CertificationResponseDTO> certifications = certificationService.getAllCertificationsByProfile(profileId);

        List<Map<String, Object>> timeline = certifications.stream()
                .map(cert -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", cert.getId());
                    item.put("name", cert.getName());
                    item.put("organization", cert.getIssuingOrganization());
                    item.put("issueDate", cert.getIssueDate());
                    item.put("expirationDate", cert.getExpirationDate());
                    item.put("doesNotExpire", cert.getDoesNotExpire());
                    item.put("isVerified", cert.getIsVerified());
                    item.put("isExpired", cert.getIsExpired());
                    return item;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(timeline);
    }

    @GetMapping("/report/{profileId}")
    @Operation(summary = "Generate certification report")
    public ResponseEntity<Map<String, Object>> generateCertificationReport(@PathVariable Long profileId) {
        log.debug("GET /api/v1/certifications/report/{} - Generating certification report", profileId);

        CertificationStatsDTO stats = certificationService.getCertificationStats(profileId);
        List<CertificationResponseDTO> activeCerts = certificationService.getActiveCertifications(profileId);
        List<CertificationResponseDTO> expiringSoon = certificationService.getCertificationsExpiringSoon(profileId, 30);
        List<String> organizations = certificationService.getDistinctOrganizations(profileId);

        Map<String, Object> report = new HashMap<>();
        report.put("generatedAt", LocalDate.now().toString());
        report.put("profileId", profileId);
        report.put("statistics", stats);
        report.put("activeCertificationsCount", activeCerts.size());
        report.put("expiringSoonCount", expiringSoon.size());
        report.put("organizations", organizations);
        report.put("summary", generateSummary(stats));

        return ResponseEntity.ok(report);
    }

    private Map<String, String> generateSummary(CertificationStatsDTO stats) {
        Map<String, String> summary = new HashMap<>();
        summary.put("total", String.valueOf(stats.getTotalCertifications()));
        summary.put("verified", String.valueOf(stats.getVerifiedCertifications()));
        summary.put("expired", String.valueOf(stats.getExpiredCertifications()));
        summary.put("expiringSoon", String.valueOf(stats.getExpiringSoonCount()));
        summary.put("neverExpire", String.valueOf(stats.getNeverExpireCount()));

        if (stats.getExpiringSoonCount() > 0) {
            summary.put("alert", String.format("%d certifications are expiring soon", stats.getExpiringSoonCount()));
        }

        return summary;
    }
}
