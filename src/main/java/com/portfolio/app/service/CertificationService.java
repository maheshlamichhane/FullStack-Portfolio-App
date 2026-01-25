package com.portfolio.app.service;

import com.portfolio.app.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CertificationService {

    // Create
    CertificationResponseDTO createCertification(CertificationDTO certificationDTO);

    // Read
    CertificationResponseDTO getCertificationById(Long id);
    List<CertificationResponseDTO> getAllCertificationsByProfile(Long profileId);
    Page<CertificationResponseDTO> getCertificationsByProfile(Long profileId, Pageable pageable);
    List<CertificationResponseDTO> getVerifiedCertifications(Long profileId);
    List<CertificationResponseDTO> getCertificationsByOrganization(Long profileId, String organization);
    List<CertificationResponseDTO> getExpiredCertifications(Long profileId);
    List<CertificationResponseDTO> getActiveCertifications(Long profileId);
    List<CertificationResponseDTO> getCertificationsExpiringSoon(Long profileId, int days);
    List<CertificationResponseDTO> searchCertifications(Long profileId, String query);
    List<CertificationSummaryDTO> getUpcomingExpirations(Long profileId, int limit);
    CertificationStatsDTO getCertificationStats(Long profileId);

    // Update
    CertificationResponseDTO updateCertification(Long id, CertificationDTO certificationDTO);
    CertificationResponseDTO verifyCertification(Long id);
    CertificationResponseDTO unverifyCertification(Long id);
    CertificationResponseDTO renewCertification(Long id, LocalDate newExpirationDate);

    // Delete
    void deleteCertification(Long id);
    void deleteAllCertificationsByProfile(Long profileId);
    void deleteExpiredCertifications(Long profileId);

    // Validation & Checks
    boolean existsByCredentialId(String credentialId, Long profileId);
    boolean isCredentialIdAvailable(String credentialId, Long profileId, Long excludeId);
    boolean isCertificationExpired(Long certificationId);
    List<CertificationResponseDTO> getExpiringCertificationsNotifications(Long profileId, int daysThreshold);

    // Statistics
    Map<String, Long> getOrganizationDistribution(Long profileId);
    List<String> getDistinctOrganizations(Long profileId);
    Integer getDaysUntilExpiration(Long certificationId);
}
