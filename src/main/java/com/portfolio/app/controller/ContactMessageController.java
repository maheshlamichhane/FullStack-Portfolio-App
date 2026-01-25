package com.portfolio.app.controller;

import com.portfolio.app.dto.*;
import com.portfolio.app.service.ContactMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/contact")
@RequiredArgsConstructor
@Tag(name = "Contact", description = "Contact message management APIs")
@Slf4j
public class ContactMessageController {

    private final ContactMessageService contactMessageService;

    @PostMapping("/public")
    @Operation(summary = "Send a contact message (public endpoint)")
    public ResponseEntity<ContactMessageResponseDTO> sendContactMessage(
            @Valid @RequestBody ContactMessageRequestDTO requestDTO) {
        log.info("POST /api/v1/contact/public - New contact message from {}", requestDTO.getEmail());
        ContactMessageResponseDTO response = contactMessageService.createMessage(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping
    @Operation(summary = "Create a new contact message")
    public ResponseEntity<ContactMessageResponseDTO> createMessage(
            @Valid @RequestBody ContactMessageDTO messageDTO) {
        log.info("POST /api/v1/contact - Creating new contact message");
        ContactMessageResponseDTO response = contactMessageService.createMessage(messageDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get contact message by ID")
    public ResponseEntity<ContactMessageResponseDTO> getMessage(@PathVariable Long id) {
        log.debug("GET /api/v1/contact/{} - Fetching contact message", id);
        ContactMessageResponseDTO message = contactMessageService.getMessageById(id);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/profile/{profileId}")
    @Operation(summary = "Get all contact messages for a profile")
    public ResponseEntity<Page<ContactMessageResponseDTO>> getMessagesByProfile(
            @PathVariable Long profileId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable,
            @RequestParam(required = false) Boolean unread,
            @RequestParam(required = false) Boolean archived,
            @RequestParam(required = false) String search) {
        log.debug("GET /api/v1/contact/profile/{} - Fetching messages", profileId);

        Page<ContactMessageResponseDTO> messages;

        if (search != null && !search.trim().isEmpty()) {
            messages = contactMessageService.searchMessages(profileId, search, pageable);
        } else if (unread != null && unread) {
            messages = contactMessageService.getUnreadMessages(profileId, pageable);
        } else if (archived != null && archived) {
            messages = contactMessageService.getArchivedMessages(profileId, pageable);
        } else {
            messages = contactMessageService.getAllMessagesByProfile(profileId, pageable);
        }

        return ResponseEntity.ok(messages);
    }

    @GetMapping("/stats/{profileId}")
    @Operation(summary = "Get contact statistics for a profile")
    public ResponseEntity<ContactStatsDTO> getContactStats(@PathVariable Long profileId) {
        log.debug("GET /api/v1/contact/stats/{} - Getting contact statistics", profileId);
        ContactStatsDTO stats = contactMessageService.getContactStats(profileId);
        return ResponseEntity.ok(stats);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a contact message")
    public ResponseEntity<ContactMessageResponseDTO> updateMessage(
            @PathVariable Long id,
            @Valid @RequestBody ContactMessageDTO messageDTO) {
        log.info("PUT /api/v1/contact/{} - Updating contact message", id);
        ContactMessageResponseDTO updatedMessage = contactMessageService.updateMessage(id, messageDTO);
        return ResponseEntity.ok(updatedMessage);
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark message as read")
    public ResponseEntity<ContactMessageResponseDTO> markAsRead(@PathVariable Long id) {
        log.info("PATCH /api/v1/contact/{}/read - Marking as read", id);
        ContactMessageResponseDTO message = contactMessageService.markAsRead(id);
        return ResponseEntity.ok(message);
    }

    @PatchMapping("/{id}/unread")
    @Operation(summary = "Mark message as unread")
    public ResponseEntity<ContactMessageResponseDTO> markAsUnread(@PathVariable Long id) {
        log.info("PATCH /api/v1/contact/{}/unread - Marking as unread", id);
        ContactMessageResponseDTO message = contactMessageService.markAsUnread(id);
        return ResponseEntity.ok(message);
    }

    @PatchMapping("/{id}/archive")
    @Operation(summary = "Archive a message")
    public ResponseEntity<ContactMessageResponseDTO> archiveMessage(@PathVariable Long id) {
        log.info("PATCH /api/v1/contact/{}/archive - Archiving message", id);
        ContactMessageResponseDTO message = contactMessageService.archiveMessage(id);
        return ResponseEntity.ok(message);
    }

    @PatchMapping("/{id}/unarchive")
    @Operation(summary = "Unarchive a message")
    public ResponseEntity<ContactMessageResponseDTO> unarchiveMessage(@PathVariable Long id) {
        log.info("PATCH /api/v1/contact/{}/unarchive - Unarchiving message", id);
        ContactMessageResponseDTO message = contactMessageService.unarchiveMessage(id);
        return ResponseEntity.ok(message);
    }

    @PatchMapping("/batch/read")
    @Operation(summary = "Mark multiple messages as read")
    public ResponseEntity<Void> markMultipleAsRead(@RequestBody List<Long> messageIds) {
        log.info("PATCH /api/v1/contact/batch/read - Marking {} messages as read", messageIds.size());
        contactMessageService.markMultipleAsRead(messageIds);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/batch/unread")
    @Operation(summary = "Mark multiple messages as unread")
    public ResponseEntity<Void> markMultipleAsUnread(@RequestBody List<Long> messageIds) {
        log.info("PATCH /api/v1/contact/batch/unread - Marking {} messages as unread", messageIds.size());
        contactMessageService.markMultipleAsUnread(messageIds);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/batch/archive")
    @Operation(summary = "Archive multiple messages")
    public ResponseEntity<Void> archiveMultipleMessages(@RequestBody List<Long> messageIds) {
        log.info("PATCH /api/v1/contact/batch/archive - Archiving {} messages", messageIds.size());
        contactMessageService.archiveMultipleMessages(messageIds);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/batch/unarchive")
    @Operation(summary = "Unarchive multiple messages")
    public ResponseEntity<Void> unarchiveMultipleMessages(@RequestBody List<Long> messageIds) {
        log.info("PATCH /api/v1/contact/batch/unarchive - Unarchiving {} messages", messageIds.size());
        contactMessageService.unarchiveMultipleMessages(messageIds);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a contact message")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        log.info("DELETE /api/v1/contact/{} - Deleting message", id);
        contactMessageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "Delete multiple messages")
    public ResponseEntity<Void> deleteMultipleMessages(@RequestBody List<Long> messageIds) {
        log.info("DELETE /api/v1/contact/batch - Deleting {} messages", messageIds.size());
        contactMessageService.deleteMultipleMessages(messageIds);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/cleanup/{profileId}")
    @Operation(summary = "Delete old archived messages")
    public ResponseEntity<Map<String, Object>> deleteOldMessages(
            @PathVariable Long profileId,
            @RequestParam(defaultValue = "30") int days) {
        log.info("DELETE /api/v1/contact/cleanup/{}?days={} - Cleaning up old messages", profileId, days);
        contactMessageService.deleteOldMessages(profileId, days);

        Map<String, Object> response = new HashMap<>();
        response.put("message", String.format("Messages older than %d days have been deleted", days));
        response.put("profileId", profileId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/unread-count/{profileId}")
    @Operation(summary = "Get unread message count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@PathVariable Long profileId) {
        log.debug("GET /api/v1/contact/unread-count/{} - Getting unread count", profileId);
        long count = contactMessageService.getUnreadCount(profileId);

        Map<String, Long> response = new HashMap<>();
        response.put("unreadCount", count);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-previous/{profileId}")
    @Operation(summary = "Check if email has previous contact")
    public ResponseEntity<Map<String, Boolean>> checkPreviousContact(
            @PathVariable Long profileId,
            @RequestParam String email) {
        log.debug("GET /api/v1/contact/check-previous/{}?email={} - Checking previous contact",
                profileId, email);
        boolean hasPrevious = contactMessageService.hasPreviousContact(email, profileId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("hasPreviousContact", hasPrevious);

        return ResponseEntity.ok(response);
    }
}
