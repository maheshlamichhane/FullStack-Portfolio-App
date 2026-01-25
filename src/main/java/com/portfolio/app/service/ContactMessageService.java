package com.portfolio.app.service;

import com.portfolio.app.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContactMessageService {

    // Create
    ContactMessageResponseDTO createMessage(ContactMessageRequestDTO requestDTO);
    ContactMessageResponseDTO createMessage(ContactMessageDTO messageDTO);

    // Read
    ContactMessageResponseDTO getMessageById(Long id);
    Page<ContactMessageResponseDTO> getAllMessagesByProfile(Long profileId, Pageable pageable);
    Page<ContactMessageResponseDTO> getUnreadMessages(Long profileId, Pageable pageable);
    Page<ContactMessageResponseDTO> getArchivedMessages(Long profileId, Pageable pageable);
    Page<ContactMessageResponseDTO> searchMessages(Long profileId, String query, Pageable pageable);
    ContactStatsDTO getContactStats(Long profileId);

    // Update
    ContactMessageResponseDTO updateMessage(Long id, ContactMessageDTO messageDTO);
    ContactMessageResponseDTO markAsRead(Long id);
    ContactMessageResponseDTO markAsUnread(Long id);
    ContactMessageResponseDTO archiveMessage(Long id);
    ContactMessageResponseDTO unarchiveMessage(Long id);
    void markMultipleAsRead(Iterable<Long> ids);
    void markMultipleAsUnread(Iterable<Long> ids);
    void archiveMultipleMessages(Iterable<Long> ids);
    void unarchiveMultipleMessages(Iterable<Long> ids);

    // Delete
    void deleteMessage(Long id);
    void deleteMultipleMessages(Iterable<Long> ids);
    void deleteOldMessages(Long profileId, int days);

    // Utility
    long getUnreadCount(Long profileId);
    boolean hasPreviousContact(String email, Long profileId);
}
