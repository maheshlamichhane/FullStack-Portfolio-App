package com.portfolio.app.dao;


import com.portfolio.app.entity.Certification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CertificationRepository extends JpaRepository<Certification, Long> {

    // Find all certifications by profile
    List<Certification> findByProfileId(Long profileId);

    // Find certifications by profile with pagination
    Page<Certification> findByProfileId(Long profileId, Pageable pageable);

    // Find verified certifications
    List<Certification> findByProfileIdAndIsVerifiedTrue(Long profileId);

    // Find certifications by organization
    List<Certification> findByProfileIdAndIssuingOrganization(Long profileId, String issuingOrganization);

    // Find certifications that never expire
    List<Certification> findByProfileIdAndDoesNotExpireTrue(Long profileId);

    // Find expired certifications
    @Query("SELECT c FROM Certification c WHERE c.profile.id = :profileId AND " +
            "c.doesNotExpire = false AND c.expirationDate < CURRENT_DATE")
    List<Certification> findExpiredCertifications(@Param("profileId") Long profileId);

    // Find certifications expiring soon (within specified days)
    @Query("SELECT c FROM Certification c WHERE c.profile.id = :profileId AND " +
            "c.doesNotExpire = false AND c.expirationDate BETWEEN CURRENT_DATE AND :futureDate")
    List<Certification> findCertificationsExpiringSoon(
            @Param("profileId") Long profileId,
            @Param("futureDate") LocalDate futureDate);

    // Find active certifications (not expired)
    @Query("SELECT c FROM Certification c WHERE c.profile.id = :profileId AND " +
            "(c.doesNotExpire = true OR c.expirationDate >= CURRENT_DATE)")
    List<Certification> findActiveCertifications(@Param("profileId") Long profileId);

    // Find certifications by name (case-insensitive)
    @Query("SELECT c FROM Certification c WHERE c.profile.id = :profileId AND " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Certification> findByProfileIdAndNameContaining(
            @Param("profileId") Long profileId,
            @Param("name") String name);

    // Find certifications issued within a date range
    List<Certification> findByProfileIdAndIssueDateBetween(
            Long profileId, LocalDate startDate, LocalDate endDate);

    // Find certifications expiring within a date range
    @Query("SELECT c FROM Certification c WHERE c.profile.id = :profileId AND " +
            "c.doesNotExpire = false AND c.expirationDate BETWEEN :startDate AND :endDate")
    List<Certification> findByProfileIdAndExpirationDateBetween(
            @Param("profileId") Long profileId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Find certification by credential ID
    Optional<Certification> findByCredentialIdAndProfileId(String credentialId, Long profileId);

    // Check if credential ID exists for profile
    boolean existsByCredentialIdAndProfileId(String credentialId, Long profileId);

    // Get distinct organizations
    @Query("SELECT DISTINCT c.issuingOrganization FROM Certification c WHERE c.profile.id = :profileId")
    List<String> findDistinctOrganizations(@Param("profileId") Long profileId);

    // Get certification count by organization
    @Query("SELECT c.issuingOrganization, COUNT(c) FROM Certification c " +
            "WHERE c.profile.id = :profileId GROUP BY c.issuingOrganization")
    List<Object[]> countCertificationsByOrganization(@Param("profileId") Long profileId);

    // Get certifications ordered by expiration date (ascending)
    @Query("SELECT c FROM Certification c WHERE c.profile.id = :profileId AND " +
            "c.doesNotExpire = false AND c.expirationDate >= CURRENT_DATE " +
            "ORDER BY c.expirationDate ASC")
    List<Certification> findUpcomingExpirations(@Param("profileId") Long profileId);

    // Get certifications ordered by issue date (descending)
    List<Certification> findByProfileIdOrderByIssueDateDesc(Long profileId);

    // Search certifications by multiple fields
    @Query("SELECT c FROM Certification c WHERE c.profile.id = :profileId AND " +
            "(LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.issuingOrganization) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.skills) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Certification> searchCertifications(
            @Param("profileId") Long profileId,
            @Param("query") String query);

    // Count certifications by verification status
    long countByProfileIdAndIsVerified(Long profileId, Boolean isVerified);

    // Count certifications that never expire
    long countByProfileIdAndDoesNotExpire(Long profileId, Boolean doesNotExpire);

    // Count expiring soon
    @Query("SELECT COUNT(c) FROM Certification c WHERE c.profile.id = :profileId AND " +
            "c.doesNotExpire = false AND c.expirationDate BETWEEN CURRENT_DATE AND :futureDate")
    long countCertificationsExpiringSoon(
            @Param("profileId") Long profileId,
            @Param("futureDate") LocalDate futureDate);
}
