package com.portfolio.app.service;

import com.portfolio.app.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;

public interface SubscriberService {

    // Subscription Management
    SubscriberResponseDTO subscribe(SubscribeRequestDTO subscribeRequest);
    SubscriberResponseDTO confirmSubscription(ConfirmSubscriptionDTO confirmRequest);
    void unsubscribe(UnsubscribeRequestDTO unsubscribeRequest);
    void unsubscribeByEmail(String email, Long profileId, String reason);
    void resendConfirmationEmail(String email, Long profileId);

    // CRUD Operations
    SubscriberResponseDTO createSubscriber(SubscriberDTO subscriberDTO);
    SubscriberResponseDTO updateSubscriber(Long id, SubscriberDTO subscriberDTO);
    SubscriberResponseDTO getSubscriberById(Long id);
    SubscriberResponseDTO getSubscriberByEmail(Long profileId, String email);
    Page<SubscriberResponseDTO> getSubscribersByProfile(Long profileId, Pageable pageable);
    List<SubscriberResponseDTO> getActiveSubscribers(Long profileId);
    List<SubscriberResponseDTO> getConfirmedSubscribers(Long profileId);
    List<SubscriberResponseDTO> searchSubscribers(Long profileId, String query);
    void deleteSubscriber(Long id);
    void deleteAllSubscribersByProfile(Long profileId);

    // Status Management
    SubscriberResponseDTO deactivateSubscriber(Long id, String reason);
    SubscriberResponseDTO reactivateSubscriber(Long id);
    SubscriberResponseDTO markAsConfirmed(Long id);

    // Statistics & Analytics
    SubscriberStatsDTO getSubscriberStats(Long profileId);
    Map<String, Long> getSourceDistribution(Long profileId);
    Double getGrowthRate(Long profileId, int days);
    List<SubscriberResponseDTO> getRecentSubscribers(Long profileId, int limit);

    // Batch Operations
    void bulkImportSubscribers(Long profileId, List<SubscribeRequestDTO> subscribers);
    void bulkUpdateStatus(List<Long> ids, Boolean isActive);
    void sendWelcomeEmail(Long subscriberId);
    void cleanupUnconfirmedSubscribers(Long profileId, int daysThreshold);

    // Validation
    boolean isEmailSubscribed(String email, Long profileId);
    boolean isEmailConfirmed(String email, Long profileId);
    String generateConfirmationToken();
}
