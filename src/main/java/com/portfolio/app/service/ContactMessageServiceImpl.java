package com.portfolio.app.service;

import com.portfolio.app.dao.ContactMessageRepository;
import com.portfolio.app.dao.ProfileRepository;
import com.portfolio.app.dto.*;
import com.portfolio.app.entity.ContactMessage;
import com.portfolio.app.entity.Profile;
import com.portfolio.app.exception.ResourceNotFoundException;
import com.portfolio.app.exception.BusinessRuleException;
import com.portfolio.app.mapper.ContactMessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactMessageServiceImpl implements ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;
    private final ProfileRepository profileRepository;
    private final ContactMessageMapper contactMessageMapper;

    @Override
    @Transactional
    public ContactMessageResponseDTO createMessage(ContactMessageRequestDTO requestDTO) {
        log.info("Creating new contact message from: {}", requestDTO.getEmail());

        // Find profile by slug or email
        Profile profile = profileRepository.findByEmail(requestDTO.getProfileSlug())
                .or(() -> profileRepository.findById(Long.valueOf(requestDTO.getProfileSlug())))
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "identifier", requestDTO.getProfileSlug()));

        ContactMessageDTO messageDTO = new ContactMessageDTO();
        messageDTO.setName(requestDTO.getName());
        messageDTO.setEmail(requestDTO.getEmail());
        messageDTO.setSubject(requestDTO.getSubject());
        messageDTO.setMessage(requestDTO.getMessage());
        messageDTO.setProfileId(profile.getId());

        return createMessage(messageDTO);
    }

    @Override
    @Transactional
    public ContactMessageResponseDTO createMessage(ContactMessageDTO messageDTO) {
        log.info("Creating new contact message for profile {} from: {}",
                messageDTO.getProfileId(), messageDTO.getEmail());

        // Validate profile exists
        Profile profile = profileRepository.findById(messageDTO.getProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "id", messageDTO.getProfileId()));

        // Check for spam/duplicate messages within last hour
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        long recentMessages = contactMessageRepository.findAll()
                .stream()
                .filter(msg -> msg.getEmail().equals(messageDTO.getEmail()) &&
                        msg.getProfile().getId().equals(messageDTO.getProfileId()) &&
                        msg.getCreatedAt().isAfter(oneHourAgo))
                .count();

        if (recentMessages >= 3) {
            throw new BusinessRuleException("Too many messages sent recently. Please try again later.");
        }

        ContactMessage contactMessage = contactMessageMapper.toEntity(messageDTO);
        contactMessage.setProfile(profile);

        ContactMessage savedMessage = contactMessageRepository.save(contactMessage);
        log.info("Contact message created successfully with ID: {}", savedMessage.getId());

        // TODO: Send notification email to profile owner
        // sendNotificationEmail(profile, savedMessage);

        return contactMessageMapper.toResponseDto(savedMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public ContactMessageResponseDTO getMessageById(Long id) {
        log.debug("Fetching contact message by ID: {}", id);

        ContactMessage contactMessage = contactMessageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ContactMessage", "id", id));

        return contactMessageMapper.toResponseDto(contactMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContactMessageResponseDTO> getAllMessagesByProfile(Long profileId, Pageable pageable) {
        log.debug("Fetching all contact messages for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return contactMessageRepository.findByProfileId(profileId, pageable)
                .map(contactMessageMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContactMessageResponseDTO> getUnreadMessages(Long profileId, Pageable pageable) {
        log.debug("Fetching unread messages for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return contactMessageRepository.findByProfileIdAndIsReadFalse(profileId, pageable)
                .map(contactMessageMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContactMessageResponseDTO> getArchivedMessages(Long profileId, Pageable pageable) {
        log.debug("Fetching archived messages for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return contactMessageRepository.findByProfileIdAndIsArchivedTrue(profileId, pageable)
                .map(contactMessageMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContactMessageResponseDTO> searchMessages(Long profileId, String query, Pageable pageable) {
        log.debug("Searching messages for profile ID: {} with query: {}", profileId, query);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        if (query == null || query.trim().isEmpty()) {
            return getAllMessagesByProfile(profileId, pageable);
        }

        return contactMessageRepository.search(profileId, query.trim(), pageable)
                .map(contactMessageMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ContactStatsDTO getContactStats(Long profileId) {
        log.debug("Getting contact statistics for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime weekStart = now.minusDays(7);

//        long totalMessages = contactMessageRepository.countByProfileId(profileId);
        long totalMessages =10l;
        long unreadMessages = contactMessageRepository.countByProfileIdAndIsReadFalse(profileId);
        long archivedMessages = contactMessageRepository.countByProfileIdAndIsArchivedTrue(profileId);
        long todayMessages = contactMessageRepository.countByProfileIdAndCreatedAtAfter(profileId, todayStart);
        long weekMessages = contactMessageRepository.countByProfileIdAndCreatedAtAfter(profileId, weekStart);

        return new ContactStatsDTO(totalMessages, unreadMessages, archivedMessages, todayMessages, weekMessages);
    }

    @Override
    @Transactional
    public ContactMessageResponseDTO updateMessage(Long id, ContactMessageDTO messageDTO) {
        log.info("Updating contact message with ID: {}", id);

        ContactMessage existingMessage = contactMessageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ContactMessage", "id", id));

        // Only allow updating certain fields
        if (messageDTO.getName() != null) {
            existingMessage.setName(messageDTO.getName());
        }
        if (messageDTO.getEmail() != null) {
            existingMessage.setEmail(messageDTO.getEmail());
        }
        if (messageDTO.getSubject() != null) {
            existingMessage.setSubject(messageDTO.getSubject());
        }
        if (messageDTO.getMessage() != null) {
            existingMessage.setMessage(messageDTO.getMessage());
        }
        if (messageDTO.getIsRead() != null) {
            existingMessage.setIsRead(messageDTO.getIsRead());
        }
        if (messageDTO.getIsArchived() != null) {
            existingMessage.setIsArchived(messageDTO.getIsArchived());
        }

        // Update profile if needed
        if (messageDTO.getProfileId() != null &&
                !existingMessage.getProfile().getId().equals(messageDTO.getProfileId())) {
            Profile profile = profileRepository.findById(messageDTO.getProfileId())
                    .orElseThrow(() -> new ResourceNotFoundException("Profile", "id", messageDTO.getProfileId()));
            existingMessage.setProfile(profile);
        }

        ContactMessage updatedMessage = contactMessageRepository.save(existingMessage);
        log.info("Contact message updated successfully: {}", id);

        return contactMessageMapper.toResponseDto(updatedMessage);
    }

    @Override
    @Transactional
    public ContactMessageResponseDTO markAsRead(Long id) {
        log.info("Marking message as read: {}", id);

        ContactMessage contactMessage = contactMessageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ContactMessage", "id", id));

        contactMessage.setIsRead(true);
        ContactMessage updatedMessage = contactMessageRepository.save(contactMessage);

        return contactMessageMapper.toResponseDto(updatedMessage);
    }

    @Override
    @Transactional
    public ContactMessageResponseDTO markAsUnread(Long id) {
        log.info("Marking message as unread: {}", id);

        ContactMessage contactMessage = contactMessageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ContactMessage", "id", id));

        contactMessage.setIsRead(false);
        ContactMessage updatedMessage = contactMessageRepository.save(contactMessage);

        return contactMessageMapper.toResponseDto(updatedMessage);
    }

    @Override
    @Transactional
    public ContactMessageResponseDTO archiveMessage(Long id) {
        log.info("Archiving message: {}", id);

        ContactMessage contactMessage = contactMessageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ContactMessage", "id", id));

        contactMessage.setIsArchived(true);
        ContactMessage updatedMessage = contactMessageRepository.save(contactMessage);

        return contactMessageMapper.toResponseDto(updatedMessage);
    }

    @Override
    @Transactional
    public ContactMessageResponseDTO unarchiveMessage(Long id) {
        log.info("Unarchiving message: {}", id);

        ContactMessage contactMessage = contactMessageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ContactMessage", "id", id));

        contactMessage.setIsArchived(false);
        ContactMessage updatedMessage = contactMessageRepository.save(contactMessage);

        return contactMessageMapper.toResponseDto(updatedMessage);
    }

    @Override
    @Transactional
    public void markMultipleAsRead(Iterable<Long> ids) {
        log.info("Marking multiple messages as read: {}", ids);

        List<ContactMessage> messages = contactMessageRepository.findAllById(ids);
        messages.forEach(msg -> msg.setIsRead(true));
        contactMessageRepository.saveAll(messages);
    }

    @Override
    @Transactional
    public void markMultipleAsUnread(Iterable<Long> ids) {
        log.info("Marking multiple messages as unread: {}", ids);

        List<ContactMessage> messages = contactMessageRepository.findAllById(ids);
        messages.forEach(msg -> msg.setIsRead(false));
        contactMessageRepository.saveAll(messages);
    }

    @Override
    @Transactional
    public void archiveMultipleMessages(Iterable<Long> ids) {
        log.info("Archiving multiple messages: {}", ids);

        List<ContactMessage> messages = contactMessageRepository.findAllById(ids);
        messages.forEach(msg -> msg.setIsArchived(true));
        contactMessageRepository.saveAll(messages);
    }

    @Override
    @Transactional
    public void unarchiveMultipleMessages(Iterable<Long> ids) {
        log.info("Unarchiving multiple messages: {}", ids);

        List<ContactMessage> messages = contactMessageRepository.findAllById(ids);
        messages.forEach(msg -> msg.setIsArchived(false));
        contactMessageRepository.saveAll(messages);
    }

    @Override
    @Transactional
    public void deleteMessage(Long id) {
        log.info("Deleting contact message with ID: {}", id);

        if (!contactMessageRepository.existsById(id)) {
            throw new ResourceNotFoundException("ContactMessage", "id", id);
        }

        contactMessageRepository.deleteById(id);
        log.info("Contact message deleted: {}", id);
    }

    @Override
    @Transactional
    public void deleteMultipleMessages(Iterable<Long> ids) {
        log.info("Deleting multiple messages: {}", ids);

        List<Long> existingIds = StreamSupport.stream(ids.spliterator(), false)
                .filter(id -> contactMessageRepository.existsById(id))
                .collect(Collectors.toList());

        contactMessageRepository.deleteAllById(existingIds);
    }

    @Override
    @Transactional
    public void deleteOldMessages(Long profileId, int days) {
        log.info("Deleting messages older than {} days for profile ID: {}", days, profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);

        // This would require a custom query in repository
        // For now, we'll implement it with a stream (not efficient for large datasets)
        List<ContactMessage> oldMessages = contactMessageRepository.findByProfileId(profileId, Pageable.unpaged())
                .getContent()
                .stream()
                .filter(msg -> msg.getCreatedAt().isBefore(cutoffDate) && msg.getIsArchived())
                .collect(Collectors.toList());

        contactMessageRepository.deleteAll(oldMessages);
        log.info("Deleted {} old messages", oldMessages.size());
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(Long profileId) {
        return contactMessageRepository.countByProfileIdAndIsReadFalse(profileId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasPreviousContact(String email, Long profileId) {
        return contactMessageRepository.existsByEmailAndProfileId(email, profileId);
    }

    // Private method for sending notification email (to be implemented)
    private void sendNotificationEmail(Profile profile, ContactMessage message) {
        // Implementation for sending email notification
        // Could use Spring Mail, SendGrid, AWS SES, etc.
        log.info("Sending notification email to {} for new contact message from {}",
                profile.getEmail(), message.getEmail());
    }
}
