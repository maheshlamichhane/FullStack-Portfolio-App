package com.portfolio.app.service;


import com.portfolio.app.dao.CertificationRepository;
import com.portfolio.app.dao.ProfileRepository;
import com.portfolio.app.dto.*;
import com.portfolio.app.entity.Certification;
import com.portfolio.app.entity.Profile;
import com.portfolio.app.exception.ResourceNotFoundException;
import com.portfolio.app.exception.BusinessRuleException;
import com.portfolio.app.mapper.CertificationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CertificationServiceImpl implements CertificationService {

    private final CertificationRepository certificationRepository;
    private final ProfileRepository profileRepository;
    private final CertificationMapper certificationMapper;

    @Override
    @Transactional
    public CertificationResponseDTO createCertification(CertificationDTO certificationDTO) {
        log.info("Creating new certification for profile {}: {}",
                certificationDTO.getProfileId(), certificationDTO.getName());

        // Validate profile exists
        Profile profile = profileRepository.findById(certificationDTO.getProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "id", certificationDTO.getProfileId()));

        // Validate dates
        validateCertificationDates(certificationDTO);

        // Check if credential ID is unique for this profile
        if (certificationDTO.getCredentialId() != null &&
                !certificationDTO.getCredentialId().trim().isEmpty() &&
                certificationRepository.existsByCredentialIdAndProfileId(
                        certificationDTO.getCredentialId(), certificationDTO.getProfileId())) {
            throw new BusinessRuleException("Credential ID '" + certificationDTO.getCredentialId() +
                    "' already exists for this profile");
        }

        Certification certification = certificationMapper.toEntity(certificationDTO);
        certification.setProfile(profile);

        Certification savedCertification = certificationRepository.save(certification);
        log.info("Certification created successfully with ID: {}", savedCertification.getId());

        return certificationMapper.toResponseDto(savedCertification);
    }

    @Override
    @Transactional(readOnly = true)
    public CertificationResponseDTO getCertificationById(Long id) {
        log.debug("Fetching certification by ID: {}", id);

        Certification certification = certificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certification", "id", id));

        return certificationMapper.toResponseDto(certification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CertificationResponseDTO> getAllCertificationsByProfile(Long profileId) {
        log.debug("Fetching all certifications for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return certificationRepository.findByProfileIdOrderByIssueDateDesc(profileId)
                .stream()
                .map(certificationMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CertificationResponseDTO> getCertificationsByProfile(Long profileId, Pageable pageable) {
        log.debug("Fetching certifications for profile ID: {} with pagination", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return certificationRepository.findByProfileId(profileId, pageable)
                .map(certificationMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CertificationResponseDTO> getVerifiedCertifications(Long profileId) {
        log.debug("Fetching verified certifications for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return certificationRepository.findByProfileIdAndIsVerifiedTrue(profileId)
                .stream()
                .map(certificationMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CertificationResponseDTO> getCertificationsByOrganization(Long profileId, String organization) {
        log.debug("Fetching certifications from organization '{}' for profile ID: {}", organization, profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return certificationRepository.findByProfileIdAndIssuingOrganization(profileId, organization)
                .stream()
                .map(certificationMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CertificationResponseDTO> getExpiredCertifications(Long profileId) {
        log.debug("Fetching expired certifications for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return certificationRepository.findExpiredCertifications(profileId)
                .stream()
                .map(certificationMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CertificationResponseDTO> getActiveCertifications(Long profileId) {
        log.debug("Fetching active certifications for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return certificationRepository.findActiveCertifications(profileId)
                .stream()
                .map(certificationMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CertificationResponseDTO> getCertificationsExpiringSoon(Long profileId, int days) {
        log.debug("Fetching certifications expiring within {} days for profile ID: {}", days, profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        LocalDate futureDate = LocalDate.now().plusDays(days);
        return certificationRepository.findCertificationsExpiringSoon(profileId, futureDate)
                .stream()
                .map(certificationMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CertificationResponseDTO> searchCertifications(Long profileId, String query) {
        log.debug("Searching certifications with query '{}' for profile ID: {}", query, profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        if (query == null || query.trim().isEmpty()) {
            return getAllCertificationsByProfile(profileId);
        }

        return certificationRepository.searchCertifications(profileId, query.trim())
                .stream()
                .map(certificationMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CertificationSummaryDTO> getUpcomingExpirations(Long profileId, int limit) {
        log.debug("Fetching {} upcoming expirations for profile ID: {}", limit, profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        Pageable pageable = PageRequest.of(0, limit);
        return certificationRepository.findUpcomingExpirations(profileId)
                .stream()
                .limit(limit)
                .map(certificationMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CertificationResponseDTO updateCertification(Long id, CertificationDTO certificationDTO) {
        log.info("Updating certification with ID: {}", id);

        Certification existingCertification = certificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certification", "id", id));

        // Validate dates
        validateCertificationDates(certificationDTO);

        // Check if credential ID is being changed and if it's available
        String newCredentialId = certificationDTO.getCredentialId();
        if (newCredentialId != null && !newCredentialId.trim().isEmpty() &&
                !newCredentialId.equals(existingCertification.getCredentialId())) {
            if (!isCredentialIdAvailable(newCredentialId, certificationDTO.getProfileId(), id)) {
                throw new BusinessRuleException("Credential ID '" + newCredentialId +
                        "' already exists for this profile");
            }
        }

        // Update profile if changed
        if (!existingCertification.getProfile().getId().equals(certificationDTO.getProfileId())) {
            Profile profile = profileRepository.findById(certificationDTO.getProfileId())
                    .orElseThrow(() -> new ResourceNotFoundException("Profile", "id", certificationDTO.getProfileId()));
            existingCertification.setProfile(profile);
        }

        certificationMapper.updateEntityFromDto(certificationDTO, existingCertification);

        Certification updatedCertification = certificationRepository.save(existingCertification);
        log.info("Certification updated successfully: {}", id);

        return certificationMapper.toResponseDto(updatedCertification);
    }

    @Override
    @Transactional
    public CertificationResponseDTO verifyCertification(Long id) {
        log.info("Verifying certification with ID: {}", id);

        Certification certification = certificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certification", "id", id));

        certification.setIsVerified(true);
        Certification updatedCertification = certificationRepository.save(certification);

        return certificationMapper.toResponseDto(updatedCertification);
    }

    @Override
    @Transactional
    public CertificationResponseDTO unverifyCertification(Long id) {
        log.info("Unverifying certification with ID: {}", id);

        Certification certification = certificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certification", "id", id));

        certification.setIsVerified(false);
        Certification updatedCertification = certificationRepository.save(certification);

        return certificationMapper.toResponseDto(updatedCertification);
    }

    @Override
    @Transactional
    public CertificationResponseDTO renewCertification(Long id, LocalDate newExpirationDate) {
        log.info("Renewing certification with ID: {} with new expiration date: {}", id, newExpirationDate);

        Certification certification = certificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certification", "id", id));

        if (Boolean.TRUE.equals(certification.getDoesNotExpire())) {
            throw new BusinessRuleException("Cannot renew a certification that never expires");
        }

        if (newExpirationDate == null) {
            throw new BusinessRuleException("New expiration date is required");
        }

        if (newExpirationDate.isBefore(LocalDate.now())) {
            throw new BusinessRuleException("New expiration date cannot be in the past");
        }

        if (certification.getExpirationDate() != null &&
                newExpirationDate.isBefore(certification.getExpirationDate())) {
            throw new BusinessRuleException("New expiration date cannot be before current expiration date");
        }

        certification.setExpirationDate(newExpirationDate);
        certification.setDoesNotExpire(false);

        Certification renewedCertification = certificationRepository.save(certification);

        return certificationMapper.toResponseDto(renewedCertification);
    }

    @Override
    @Transactional
    public void deleteCertification(Long id) {
        log.info("Deleting certification with ID: {}", id);

        if (!certificationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Certification", "id", id);
        }

        certificationRepository.deleteById(id);
        log.info("Certification deleted: {}", id);
    }

    @Override
    @Transactional
    public void deleteAllCertificationsByProfile(Long profileId) {
        log.info("Deleting all certifications for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        certificationRepository.deleteByProfileId(profileId);
    }

    @Override
    @Transactional
    public void deleteExpiredCertifications(Long profileId) {
        log.info("Deleting expired certifications for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        List<Certification> expiredCertifications = certificationRepository.findExpiredCertifications(profileId);
        certificationRepository.deleteAll(expiredCertifications);

        log.info("Deleted {} expired certifications", expiredCertifications.size());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCredentialId(String credentialId, Long profileId) {
        return certificationRepository.existsByCredentialIdAndProfileId(credentialId, profileId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCredentialIdAvailable(String credentialId, Long profileId, Long excludeId) {
        if (credentialId == null || credentialId.trim().isEmpty()) {
            return true;
        }

        Optional<Certification> certification = certificationRepository
                .findByCredentialIdAndProfileId(credentialId, profileId);

        if (certification.isEmpty()) {
            return true;
        }

        return excludeId != null && certification.get().getId().equals(excludeId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCertificationExpired(Long certificationId) {
        Certification certification = certificationRepository.findById(certificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Certification", "id", certificationId));

        if (Boolean.TRUE.equals(certification.getDoesNotExpire())) {
            return false;
        }

        if (certification.getExpirationDate() == null) {
            return false;
        }

        return certification.getExpirationDate().isBefore(LocalDate.now());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CertificationResponseDTO> getExpiringCertificationsNotifications(Long profileId, int daysThreshold) {
        log.debug("Getting certifications expiring within {} days for notifications", daysThreshold);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        LocalDate futureDate = LocalDate.now().plusDays(daysThreshold);
        return certificationRepository.findCertificationsExpiringSoon(profileId, futureDate)
                .stream()
                .filter(cert -> !Boolean.TRUE.equals(cert.getDoesNotExpire()))
                .map(certificationMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CertificationStatsDTO getCertificationStats(Long profileId) {
        log.debug("Getting certification statistics for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        List<Certification> certifications = certificationRepository.findByProfileId(profileId);

        if (certifications.isEmpty()) {
            return new CertificationStatsDTO(0L, 0L, 0L, 0L, 0L, Map.of(), 0);
        }

        // Calculate statistics
        long totalCertifications = certifications.size();
        long verifiedCertifications = certifications.stream()
                .filter(c -> Boolean.TRUE.equals(c.getIsVerified()))
                .count();

        long expiredCertifications = certifications.stream()
                .filter(c -> !Boolean.TRUE.equals(c.getDoesNotExpire()))
                .filter(c -> c.getExpirationDate() != null)
                .filter(c -> c.getExpirationDate().isBefore(LocalDate.now()))
                .count();

        long neverExpireCount = certifications.stream()
                .filter(c -> Boolean.TRUE.equals(c.getDoesNotExpire()))
                .count();

        // Count expiring soon (within 30 days)
        LocalDate thirtyDaysFromNow = LocalDate.now().plusDays(30);
        long expiringSoonCount = certifications.stream()
                .filter(c -> !Boolean.TRUE.equals(c.getDoesNotExpire()))
                .filter(c -> c.getExpirationDate() != null)
                .filter(c -> !c.getExpirationDate().isBefore(LocalDate.now()))
                .filter(c -> c.getExpirationDate().isBefore(thirtyDaysFromNow))
                .count();

        // Organization distribution
        Map<String, Long> organizationDistribution = certifications.stream()
                .collect(Collectors.groupingBy(
                        Certification::getIssuingOrganization,
                        Collectors.counting()
                ));

        // Calculate average validity months for certifications with expiration dates
        double averageValidityMonths = certifications.stream()
                .filter(c -> !Boolean.TRUE.equals(c.getDoesNotExpire()))
                .filter(c -> c.getIssueDate() != null && c.getExpirationDate() != null)
                .mapToLong(c -> java.time.temporal.ChronoUnit.MONTHS.between(
                        c.getIssueDate().withDayOfMonth(1),
                        c.getExpirationDate().withDayOfMonth(1)
                ))
                .average()
                .orElse(0.0);

        return new CertificationStatsDTO(
                totalCertifications,
                verifiedCertifications,
                expiredCertifications,
                expiringSoonCount,
                neverExpireCount,
                organizationDistribution,
                (int) Math.round(averageValidityMonths)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getOrganizationDistribution(Long profileId) {
        List<Object[]> results = certificationRepository.countCertificationsByOrganization(profileId);

        Map<String, Long> distribution = new HashMap<>();
        for (Object[] result : results) {
            distribution.put((String) result[0], (Long) result[1]);
        }

        return distribution;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getDistinctOrganizations(Long profileId) {
        return certificationRepository.findDistinctOrganizations(profileId);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getDaysUntilExpiration(Long certificationId) {
        Certification certification = certificationRepository.findById(certificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Certification", "id", certificationId));

        if (Boolean.TRUE.equals(certification.getDoesNotExpire()) ||
                certification.getExpirationDate() == null) {
            return null;
        }

        long days = java.time.temporal.ChronoUnit.DAYS.between(
                LocalDate.now(), certification.getExpirationDate());
        return (int) days;
    }

    private void validateCertificationDates(CertificationDTO certificationDTO) {
        if (certificationDTO.getIssueDate() == null) {
            throw new BusinessRuleException("Issue date is required");
        }

        if (certificationDTO.getIssueDate().isAfter(LocalDate.now())) {
            throw new BusinessRuleException("Issue date cannot be in the future");
        }

        if (Boolean.FALSE.equals(certificationDTO.getDoesNotExpire())) {
            if (certificationDTO.getExpirationDate() == null) {
                throw new BusinessRuleException("Expiration date is required when 'doesNotExpire' is false");
            }

            if (certificationDTO.getExpirationDate().isBefore(certificationDTO.getIssueDate())) {
                throw new BusinessRuleException("Expiration date cannot be before issue date");
            }

            if (certificationDTO.getExpirationDate().isBefore(LocalDate.now())) {
                log.warn("Certification expiration date is in the past: {}", certificationDTO.getExpirationDate());
            }
        } else {
            // If doesNotExpire is true, expiration date should be null
            certificationDTO.setExpirationDate(null);
        }
    }
}
