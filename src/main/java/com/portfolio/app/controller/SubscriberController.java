package com.portfolio.app.controller;

import com.portfolio.app.dto.*;
import com.portfolio.app.service.SubscriberService;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/subscribers")
@RequiredArgsConstructor
@Tag(name = "Subscriber", description = "Newsletter subscriber management APIs")
@Slf4j
public class SubscriberController {

    private final SubscriberService subscriberService;

    @PostMapping("/subscribe")
    @Operation(summary = "Subscribe to newsletter (public endpoint)")
    public ResponseEntity<SubscriberResponseDTO> subscribe(
            @Valid @RequestBody SubscribeRequestDTO subscribeRequest) {
        log.info("POST /api/v1/subscribers/subscribe - New subscription from: {}", subscribeRequest.getEmail());
        SubscriberResponseDTO subscriber = subscriberService.subscribe(subscribeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(subscriber);
    }

    @PostMapping("/confirm")
    @Operation(summary = "Confirm subscription (public endpoint)")
    public ResponseEntity<SubscriberResponseDTO> confirmSubscription(
            @Valid @RequestBody ConfirmSubscriptionDTO confirmRequest) {
        log.info("POST /api/v1/subscribers/confirm - Confirming subscription for: {}", confirmRequest.getEmail());
        SubscriberResponseDTO subscriber = subscriberService.confirmSubscription(confirmRequest);
        return ResponseEntity.ok(subscriber);
    }

    @PostMapping("/unsubscribe")
    @Operation(summary = "Unsubscribe from newsletter (public endpoint)")
    public ResponseEntity<Map<String, String>> unsubscribe(
            @Valid @RequestBody UnsubscribeRequestDTO unsubscribeRequest) {
        log.info("POST /api/v1/subscribers/unsubscribe - Unsubscribing: {}", unsubscribeRequest.getEmail());
        subscriberService.unsubscribe(unsubscribeRequest);

        Map<String, String> response = new HashMap<>();
        response.put("message", "You have been unsubscribed successfully");
        response.put("email", unsubscribeRequest.getEmail());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend-confirmation")
    @Operation(summary = "Resend confirmation email")
    public ResponseEntity<Map<String, String>> resendConfirmationEmail(
            @RequestParam String email,
            @RequestParam Long profileId) {
        log.info("POST /api/v1/subscribers/resend-confirmation - Resending confirmation to: {}", email);
        subscriberService.resendConfirmationEmail(email, profileId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Confirmation email has been resent");
        response.put("email", email);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create a new subscriber (admin)")
    public ResponseEntity<SubscriberResponseDTO> createSubscriber(
            @Valid @RequestBody SubscriberDTO subscriberDTO) {
        log.info("POST /api/v1/subscribers - Creating new subscriber: {}", subscriberDTO.getEmail());
        SubscriberResponseDTO subscriber = subscriberService.createSubscriber(subscriberDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(subscriber);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a subscriber")
    public ResponseEntity<SubscriberResponseDTO> updateSubscriber(
            @PathVariable Long id,
            @Valid @RequestBody SubscriberDTO subscriberDTO) {
        log.info("PUT /api/v1/subscribers/{} - Updating subscriber", id);
        SubscriberResponseDTO subscriber = subscriberService.updateSubscriber(id, subscriberDTO);
        return ResponseEntity.ok(subscriber);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get subscriber by ID")
    public ResponseEntity<SubscriberResponseDTO> getSubscriber(@PathVariable Long id) {
        log.debug("GET /api/v1/subscribers/{} - Fetching subscriber", id);
        SubscriberResponseDTO subscriber = subscriberService.getSubscriberById(id);
        return ResponseEntity.ok(subscriber);
    }

    @GetMapping("/email")
    @Operation(summary = "Get subscriber by email")
    public ResponseEntity<SubscriberResponseDTO> getSubscriberByEmail(
            @RequestParam Long profileId,
            @RequestParam String email) {
        log.debug("GET /api/v1/subscribers/email?profileId={}&email={} - Fetching subscriber by email",
                profileId, email);
        SubscriberResponseDTO subscriber = subscriberService.getSubscriberByEmail(profileId, email);
        return ResponseEntity.ok(subscriber);
    }

    @GetMapping("/profile/{profileId}")
    @Operation(summary = "Get all subscribers for a profile")
    public ResponseEntity<Page<SubscriberResponseDTO>> getSubscribersByProfile(
            @PathVariable Long profileId,
            @PageableDefault(size = 50, sort = "subscribedAt", direction = Sort.Direction.DESC)
            Pageable pageable,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Boolean confirmed,
            @RequestParam(required = false) String source) {
        log.debug("GET /api/v1/subscribers/profile/{} - Fetching subscribers", profileId);

        // For filtered queries, you'd need to implement a custom repository method
        // For now, we'll use the basic paginated method
        Page<SubscriberResponseDTO> subscribers = subscriberService.getSubscribersByProfile(profileId, pageable);
        return ResponseEntity.ok(subscribers);
    }

    @GetMapping("/profile/{profileId}/active")
    @Operation(summary = "Get active subscribers")
    public ResponseEntity<List<SubscriberResponseDTO>> getActiveSubscribers(@PathVariable Long profileId) {
        log.debug("GET /api/v1/subscribers/profile/{}/active - Getting active subscribers", profileId);
        List<SubscriberResponseDTO> subscribers = subscriberService.getActiveSubscribers(profileId);
        return ResponseEntity.ok(subscribers);
    }

    @GetMapping("/profile/{profileId}/confirmed")
    @Operation(summary = "Get confirmed subscribers")
    public ResponseEntity<List<SubscriberResponseDTO>> getConfirmedSubscribers(@PathVariable Long profileId) {
        log.debug("GET /api/v1/subscribers/profile/{}/confirmed - Getting confirmed subscribers", profileId);
        List<SubscriberResponseDTO> subscribers = subscriberService.getConfirmedSubscribers(profileId);
        return ResponseEntity.ok(subscribers);
    }

    @GetMapping("/profile/{profileId}/search")
    @Operation(summary = "Search subscribers")
    public ResponseEntity<List<SubscriberResponseDTO>> searchSubscribers(
            @PathVariable Long profileId,
            @RequestParam String query) {
        log.debug("GET /api/v1/subscribers/profile/{}/search?query={} - Searching subscribers", profileId, query);
        List<SubscriberResponseDTO> subscribers = subscriberService.searchSubscribers(profileId, query);
        return ResponseEntity.ok(subscribers);
    }

    @GetMapping("/profile/{profileId}/recent")
    @Operation(summary = "Get recent subscribers")
    public ResponseEntity<List<SubscriberResponseDTO>> getRecentSubscribers(
            @PathVariable Long profileId,
            @RequestParam(defaultValue = "10") int limit) {
        log.debug("GET /api/v1/subscribers/profile/{}/recent?limit={} - Getting recent subscribers",
                profileId, limit);
        List<SubscriberResponseDTO> subscribers = subscriberService.getRecentSubscribers(profileId, limit);
        return ResponseEntity.ok(subscribers);
    }

    @GetMapping("/stats/{profileId}")
    @Operation(summary = "Get subscriber statistics")
    public ResponseEntity<SubscriberStatsDTO> getSubscriberStats(@PathVariable Long profileId) {
        log.debug("GET /api/v1/subscribers/stats/{} - Getting subscriber statistics", profileId);
        SubscriberStatsDTO stats = subscriberService.getSubscriberStats(profileId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/source-distribution/{profileId}")
    @Operation(summary = "Get subscription source distribution")
    public ResponseEntity<Map<String, Long>> getSourceDistribution(@PathVariable Long profileId) {
        log.debug("GET /api/v1/subscribers/source-distribution/{} - Getting source distribution", profileId);
        Map<String, Long> distribution = subscriberService.getSourceDistribution(profileId);
        return ResponseEntity.ok(distribution);
    }

    @GetMapping("/growth-rate/{profileId}")
    @Operation(summary = "Get growth rate")
    public ResponseEntity<Map<String, Object>> getGrowthRate(
            @PathVariable Long profileId,
            @RequestParam(defaultValue = "30") int days) {
        log.debug("GET /api/v1/subscribers/growth-rate/{}?days={} - Getting growth rate", profileId, days);
        Double growthRate = subscriberService.getGrowthRate(profileId, days);

        Map<String, Object> response = new HashMap<>();
        response.put("profileId", profileId);
        response.put("periodDays", days);
        response.put("growthRate", growthRate);
        response.put("growthRateFormatted", String.format("%.2f%%", growthRate));

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate a subscriber")
    public ResponseEntity<SubscriberResponseDTO> deactivateSubscriber(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        log.info("PATCH /api/v1/subscribers/{}/deactivate - Deactivating subscriber", id);
        SubscriberResponseDTO subscriber = subscriberService.deactivateSubscriber(id, reason);
        return ResponseEntity.ok(subscriber);
    }

    @PatchMapping("/{id}/reactivate")
    @Operation(summary = "Reactivate a subscriber")
    public ResponseEntity<SubscriberResponseDTO> reactivateSubscriber(@PathVariable Long id) {
        log.info("PATCH /api/v1/subscribers/{}/reactivate - Reactivating subscriber", id);
        SubscriberResponseDTO subscriber = subscriberService.reactivateSubscriber(id);
        return ResponseEntity.ok(subscriber);
    }

    @PatchMapping("/{id}/confirm")
    @Operation(summary = "Mark subscriber as confirmed")
    public ResponseEntity<SubscriberResponseDTO> markAsConfirmed(@PathVariable Long id) {
        log.info("PATCH /api/v1/subscribers/{}/confirm - Marking as confirmed", id);
        SubscriberResponseDTO subscriber = subscriberService.markAsConfirmed(id);
        return ResponseEntity.ok(subscriber);
    }

    @PostMapping("/bulk/import/{profileId}")
    @Operation(summary = "Bulk import subscribers")
    public ResponseEntity<Map<String, Object>> bulkImportSubscribers(
            @PathVariable Long profileId,
            @Valid @RequestBody List<SubscribeRequestDTO> subscribers) {
        log.info("POST /api/v1/subscribers/bulk/import/{} - Bulk importing {} subscribers",
                profileId, subscribers.size());
        subscriberService.bulkImportSubscribers(profileId, subscribers);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Bulk import initiated");
        response.put("profileId", profileId);
        response.put("subscribersCount", subscribers.size());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/bulk/status")
    @Operation(summary = "Bulk update subscriber status")
    public ResponseEntity<Void> bulkUpdateStatus(
            @RequestBody List<Long> ids,
            @RequestParam Boolean isActive) {
        log.info("PATCH /api/v1/subscribers/bulk/status - Updating status for {} subscribers", ids.size());
        subscriberService.bulkUpdateStatus(ids, isActive);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/welcome-email")
    @Operation(summary = "Send welcome email to subscriber")
    public ResponseEntity<Map<String, String>> sendWelcomeEmail(@PathVariable Long id) {
        log.info("POST /api/v1/subscribers/{}/welcome-email - Sending welcome email", id);
        subscriberService.sendWelcomeEmail(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Welcome email sent");
        response.put("subscriberId", id.toString());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-subscription")
    @Operation(summary = "Check if email is subscribed")
    public ResponseEntity<Map<String, Boolean>> checkSubscription(
            @RequestParam String email,
            @RequestParam Long profileId) {
        log.debug("GET /api/v1/subscribers/check-subscription?email={}&profileId={} - Checking subscription",
                email, profileId);
        boolean isSubscribed = subscriberService.isEmailSubscribed(email, profileId);
        boolean isConfirmed = subscriberService.isEmailConfirmed(email, profileId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("isSubscribed", isSubscribed);
        response.put("isConfirmed", isConfirmed);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a subscriber")
    public ResponseEntity<Void> deleteSubscriber(@PathVariable Long id) {
        log.info("DELETE /api/v1/subscribers/{} - Deleting subscriber", id);
        subscriberService.deleteSubscriber(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/profile/{profileId}")
    @Operation(summary = "Delete all subscribers for a profile")
    public ResponseEntity<Void> deleteAllSubscribersByProfile(@PathVariable Long profileId) {
        log.info("DELETE /api/v1/subscribers/profile/{} - Deleting all subscribers", profileId);
        subscriberService.deleteAllSubscribersByProfile(profileId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/cleanup/{profileId}")
    @Operation(summary = "Clean up unconfirmed subscribers")
    public ResponseEntity<Map<String, Object>> cleanupUnconfirmedSubscribers(
            @PathVariable Long profileId,
            @RequestParam(defaultValue = "7") int daysThreshold) {
        log.info("POST /api/v1/subscribers/cleanup/{} - Cleaning up unconfirmed subscribers older than {} days",
                profileId, daysThreshold);
        subscriberService.cleanupUnconfirmedSubscribers(profileId, daysThreshold);

        Map<String, Object> response = new HashMap<>();
        response.put("message", String.format("Unconfirmed subscribers older than %d days have been cleaned up", daysThreshold));
        response.put("profileId", profileId);
        response.put("daysThreshold", daysThreshold);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/export/{profileId}")
    @Operation(summary = "Export subscribers")
    public ResponseEntity<Map<String, Object>> exportSubscribers(
            @PathVariable Long profileId,
            @RequestParam(defaultValue = "csv") String format) {
        log.debug("GET /api/v1/subscribers/export/{}?format={} - Exporting subscribers", profileId, format);

        List<SubscriberResponseDTO> subscribers = subscriberService.getActiveSubscribers(profileId);
        SubscriberStatsDTO stats = subscriberService.getSubscriberStats(profileId);

        Map<String, Object> exportData = new HashMap<>();
        exportData.put("generatedAt", java.time.LocalDateTime.now().toString());
        exportData.put("profileId", profileId);
        exportData.put("totalSubscribers", subscribers.size());
        exportData.put("subscribers", subscribers);
        exportData.put("statistics", stats);
        exportData.put("format", format);

        // Generate CSV if requested
        if ("csv".equalsIgnoreCase(format)) {
            exportData.put("csvContent", generateCsvContent(subscribers));
        }

        return ResponseEntity.ok(exportData);
    }

    @GetMapping("/health/{profileId}")
    @Operation(summary = "Get subscription list health")
    public ResponseEntity<Map<String, Object>> getSubscriptionHealth(@PathVariable Long profileId) {
        log.debug("GET /api/v1/subscribers/health/{} - Getting subscription health", profileId);

        SubscriberStatsDTO stats = subscriberService.getSubscriberStats(profileId);

        Map<String, Object> health = new HashMap<>();
        health.put("profileId", profileId);
        health.put("totalSubscribers", stats.getTotalSubscribers());
        health.put("activeSubscribers", stats.getActiveSubscribers());
        health.put("confirmedRate", calculatePercentage(stats.getConfirmedSubscribers(), stats.getActiveSubscribers()));
        health.put("unsubscribeRate", calculatePercentage(stats.getUnsubscribedCount(), stats.getTotalSubscribers()));
        health.put("growthRate", stats.getGrowthRate());

        // Health status
        double confirmedRate = calculatePercentage(stats.getConfirmedSubscribers(), stats.getActiveSubscribers());
        if (confirmedRate > 80) {
            health.put("status", "HEALTHY");
            health.put("statusColor", "green");
        } else if (confirmedRate > 50) {
            health.put("status", "MODERATE");
            health.put("statusColor", "yellow");
        } else {
            health.put("status", "POOR");
            health.put("statusColor", "red");
        }

        health.put("recommendations", generateRecommendations(stats));

        return ResponseEntity.ok(health);
    }

    private String generateCsvContent(List<SubscriberResponseDTO> subscribers) {
        StringBuilder csv = new StringBuilder();
        csv.append("Email,Name,Subscription Date,Source,Status,Confirmed\n");

        for (SubscriberResponseDTO subscriber : subscribers) {
            csv.append(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                    subscriber.getEmail(),
                    subscriber.getName() != null ? subscriber.getName() : "",
                    subscriber.getSubscribedAt() != null ? subscriber.getSubscribedAt().toString() : "",
                    subscriber.getSubscriptionSource() != null ? subscriber.getSubscriptionSource() : "",
                    Boolean.TRUE.equals(subscriber.getIsActive()) ? "Active" : "Inactive",
                    Boolean.TRUE.equals(subscriber.getIsConfirmed()) ? "Yes" : "No"
            ));
        }

        return csv.toString();
    }

    private double calculatePercentage(long part, long total) {
        if (total == 0) return 0.0;
        return (part * 100.0) / total;
    }

    private List<String> generateRecommendations(SubscriberStatsDTO stats) {
        List<String> recommendations = new ArrayList<>();

        double confirmedRate = calculatePercentage(stats.getConfirmedSubscribers(), stats.getActiveSubscribers());
        if (confirmedRate < 50) {
            recommendations.add("Low confirmation rate: Consider resending confirmation emails to unconfirmed subscribers");
        }

        if (stats.getGrowthRate() < 0) {
            recommendations.add("Negative growth: Review subscription sources and improve signup forms");
        }

        if (stats.getTodaySubscriptions() == 0) {
            recommendations.add("No new subscriptions today: Promote newsletter on social media or blog");
        }

        return recommendations;
    }
}
