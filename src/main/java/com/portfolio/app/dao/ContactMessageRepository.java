package com.portfolio.app.dao;

import com.portfolio.app.entity.ContactMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {

    // Find all messages for a profile
    Page<ContactMessage> findByProfileId(Long profileId, Pageable pageable);

    // Find unread messages for a profile
    Page<ContactMessage> findByProfileIdAndIsReadFalse(Long profileId, Pageable pageable);

    // Find archived messages for a profile
    Page<ContactMessage> findByProfileIdAndIsArchivedTrue(Long profileId, Pageable pageable);

    // Find non-archived messages for a profile
    Page<ContactMessage> findByProfileIdAndIsArchivedFalse(Long profileId, Pageable pageable);

    // Find messages by email
    Page<ContactMessage> findByEmail(String email, Pageable pageable);

    // Find messages by email and profile
    Page<ContactMessage> findByEmailAndProfileId(String email, Long profileId, Pageable pageable);

    // Search messages by content
    @Query("SELECT cm FROM ContactMessage cm WHERE " +
            "(LOWER(cm.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(cm.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(cm.subject) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(cm.message) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
            "cm.profile.id = :profileId")
    Page<ContactMessage> search(
            @Param("profileId") Long profileId,
            @Param("query") String query,
            Pageable pageable);

    // Count unread messages for a profile
    long countByProfileIdAndIsReadFalse(Long profileId);

    // Count archived messages for a profile
    long countByProfileIdAndIsArchivedTrue(Long profileId);

    // Count today's messages for a profile
    long countByProfileIdAndCreatedAtAfter(Long profileId, LocalDateTime date);

    // Count messages by read status
    long countByProfileIdAndIsRead(Long profileId, Boolean isRead);

    // Count messages by archived status
    long countByProfileIdAndIsArchived(Long profileId, Boolean isArchived);

    // Check if email exists for a profile (to identify returning contacts)
    boolean existsByEmailAndProfileId(String email, Long profileId);

    // Find latest message from an email for a profile
    Optional<ContactMessage> findFirstByEmailAndProfileIdOrderByCreatedAtDesc(
            String email, Long profileId);
}
