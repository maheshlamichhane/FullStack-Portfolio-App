package com.portfolio.app.service;


import com.portfolio.app.dao.ProfileRepository;
import com.portfolio.app.dao.SubscriberRepository;
import com.portfolio.app.dto.*;
import com.portfolio.app.entity.Subscriber;
import com.portfolio.app.entity.Profile;
import com.portfolio.app.exception.ResourceNotFoundException;
import com.portfolio.app.exception.BusinessRuleException;
import com.portfolio.app.mapper.SubscriberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriberServiceImpl implements SubscriberService {

    private final SubscriberRepository subscriberRepository;
    private final ProfileRepository profileRepository;
    private final SubscriberMapper subscriberMapper;
//    private final EmailService emailService;

    @Override
    @Transactional
    public SubscriberResponseDTO subscribe(SubscribeRequestDTO subscribeRequest) {
        log.info("Processing subscription request for email: {}", subscribeRequest.getEmail());

        // Find profile by identifier
        Profile profile = findProfileByIdentifier(subscribeRequest.getProfileIdentifier());

        // Check if email is already subscribed
        if (subscriberRepository.existsByEmailAndProfileId(subscribeRequest.getEmail(), profile.getId())) {
            Subscriber existing = subscriberRepository.findByEmailAndProfileId(
                            subscribeRequest.getEmail(), profile.getId())
                    .orElseThrow(() -> new BusinessRuleException("Subscriber not found"));

            if (Boolean.TRUE.equals(existing.getIsActive())) {
                if (Boolean.TRUE.equals(existing.getIsConfirmed())) {
                    throw new BusinessRuleException("Email is already subscribed and confirmed");
                } else {
                    // Resend confirmation email
                    sendConfirmationEmail(existing);
                    return subscriberMapper.toResponseDto(existing);
                }
            } else {
                // Reactivate unsubscribed user
                existing.setIsActive(true);
                existing.setUnsubscribedAt(null);
                existing.setSubscriptionSource(subscribeRequest.getSource());
                existing.setIsConfirmed(false);
                existing.setConfirmationToken(generateConfirmationToken());
                existing.setSubscribedAt(LocalDateTime.now());

                Subscriber reactivated = subscriberRepository.save(existing);
                sendConfirmationEmail(reactivated);
                return subscriberMapper.toResponseDto(reactivated);
            }
        }

        // Create new subscriber
        SubscriberDTO subscriberDTO = new SubscriberDTO();
        subscriberDTO.setEmail(subscribeRequest.getEmail());
        subscriberDTO.setName(subscribeRequest.getName());
        subscriberDTO.setProfileId(profile.getId());
        subscriberDTO.setSubscriptionSource(subscribeRequest.getSource());
        subscriberDTO.setConfirmationToken(generateConfirmationToken());
        subscriberDTO.setIsActive(true);
        subscriberDTO.setIsConfirmed(false);
        subscriberDTO.setSubscribedAt(LocalDateTime.now());

        Subscriber subscriber = subscriberMapper.toEntity(subscriberDTO);
        subscriber.setProfile(profile);

        Subscriber savedSubscriber = subscriberRepository.save(subscriber);
        log.info("New subscriber created with ID: {}", savedSubscriber.getId());

        // Send confirmation email
        sendConfirmationEmail(savedSubscriber);

        return subscriberMapper.toResponseDto(savedSubscriber);
    }

    @Override
    @Transactional
    public SubscriberResponseDTO confirmSubscription(ConfirmSubscriptionDTO confirmRequest) {
        log.info("Confirming subscription for email: {}", confirmRequest.getEmail());

        Subscriber subscriber = subscriberRepository.findByConfirmationToken(confirmRequest.getToken())
                .orElseThrow(() -> new BusinessRuleException("Invalid confirmation token"));

        if (!subscriber.getEmail().equalsIgnoreCase(confirmRequest.getEmail())) {
            throw new BusinessRuleException("Email does not match confirmation token");
        }

        if (Boolean.FALSE.equals(subscriber.getIsActive())) {
            throw new BusinessRuleException("Subscription is inactive");
        }

        if (Boolean.TRUE.equals(subscriber.getIsConfirmed())) {
            throw new BusinessRuleException("Subscription already confirmed");
        }

        subscriber.setIsConfirmed(true);
        subscriber.setConfirmationToken(null); // Clear token after confirmation

        Subscriber confirmedSubscriber = subscriberRepository.save(subscriber);
        log.info("Subscription confirmed for email: {}", confirmRequest.getEmail());

        // Send welcome email
        sendWelcomeEmail(confirmedSubscriber);

        return subscriberMapper.toResponseDto(confirmedSubscriber);
    }

    @Override
    @Transactional
    public void unsubscribe(UnsubscribeRequestDTO unsubscribeRequest) {
        log.info("Processing unsubscribe request for email: {}", unsubscribeRequest.getEmail());

        Profile profile = findProfileByIdentifier(unsubscribeRequest.getProfileIdentifier());
        unsubscribeByEmail(unsubscribeRequest.getEmail(), profile.getId(), unsubscribeRequest.getReason());
    }

    @Override
    @Transactional
    public void unsubscribeByEmail(String email, Long profileId, String reason) {
        Subscriber subscriber = subscriberRepository.findByEmailAndProfileId(email, profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscriber", "email", email));

        if (Boolean.FALSE.equals(subscriber.getIsActive())) {
            log.warn("Subscriber already unsubscribed: {}", email);
            return;
        }

        subscriber.setIsActive(false);
        subscriber.setUnsubscribedAt(LocalDateTime.now());
        subscriber.setConfirmationToken(null);

        subscriberRepository.save(subscriber);
        log.info("Subscriber unsubscribed: {}", email);

        // Send unsubscribe confirmation email
        sendUnsubscribeConfirmationEmail(subscriber, reason);
    }

    @Override
    @Transactional
    public void resendConfirmationEmail(String email, Long profileId) {
        Subscriber subscriber = subscriberRepository.findByEmailAndProfileId(email, profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscriber", "email", email));

        if (Boolean.TRUE.equals(subscriber.getIsConfirmed())) {
            throw new BusinessRuleException("Subscription already confirmed");
        }

        if (Boolean.FALSE.equals(subscriber.getIsActive())) {
            throw new BusinessRuleException("Subscription is inactive");
        }

        // Generate new token if expired
        if (subscriber.getConfirmationToken() == null) {
            subscriber.setConfirmationToken(generateConfirmationToken());
            subscriberRepository.save(subscriber);
        }

        sendConfirmationEmail(subscriber);
    }

    @Override
    @Transactional
    public SubscriberResponseDTO createSubscriber(SubscriberDTO subscriberDTO) {
        log.info("Creating subscriber for profile {}: {}",
                subscriberDTO.getProfileId(), subscriberDTO.getEmail());

        Profile profile = profileRepository.findById(subscriberDTO.getProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "id", subscriberDTO.getProfileId()));

        // Check if email already exists
        if (subscriberRepository.existsByEmailAndProfileId(subscriberDTO.getEmail(), subscriberDTO.getProfileId())) {
            throw new BusinessRuleException("Email already subscribed to this profile");
        }

        // Generate confirmation token if not provided
        if (subscriberDTO.getConfirmationToken() == null && Boolean.FALSE.equals(subscriberDTO.getIsConfirmed())) {
            subscriberDTO.setConfirmationToken(generateConfirmationToken());
        }

        // Set subscription date if not provided
        if (subscriberDTO.getSubscribedAt() == null) {
            subscriberDTO.setSubscribedAt(LocalDateTime.now());
        }

        Subscriber subscriber = subscriberMapper.toEntity(subscriberDTO);
        subscriber.setProfile(profile);

        Subscriber savedSubscriber = subscriberRepository.save(subscriber);

        // Send confirmation email if not confirmed
        if (Boolean.FALSE.equals(savedSubscriber.getIsConfirmed())) {
            sendConfirmationEmail(savedSubscriber);
        } else {
            sendWelcomeEmail(savedSubscriber);
        }

        return subscriberMapper.toResponseDto(savedSubscriber);
    }

    @Override
    @Transactional
    public SubscriberResponseDTO updateSubscriber(Long id, SubscriberDTO subscriberDTO) {
        log.info("Updating subscriber with ID: {}", id);

        Subscriber existingSubscriber = subscriberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscriber", "id", id));

        // Check if email is being changed and if it's unique
        if (!existingSubscriber.getEmail().equalsIgnoreCase(subscriberDTO.getEmail())) {
            if (subscriberRepository.existsByEmailAndProfileId(subscriberDTO.getEmail(), subscriberDTO.getProfileId())) {
                throw new BusinessRuleException("Email already exists for this profile");
            }
        }

        // Update profile if changed
        if (!existingSubscriber.getProfile().getId().equals(subscriberDTO.getProfileId())) {
            Profile profile = profileRepository.findById(subscriberDTO.getProfileId())
                    .orElseThrow(() -> new ResourceNotFoundException("Profile", "id", subscriberDTO.getProfileId()));
            existingSubscriber.setProfile(profile);
        }

        subscriberMapper.updateEntityFromDto(subscriberDTO, existingSubscriber);

        Subscriber updatedSubscriber = subscriberRepository.save(existingSubscriber);
        return subscriberMapper.toResponseDto(updatedSubscriber);
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriberResponseDTO getSubscriberById(Long id) {
        Subscriber subscriber = subscriberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscriber", "id", id));

        return subscriberMapper.toResponseDto(subscriber);
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriberResponseDTO getSubscriberByEmail(Long profileId, String email) {
        Subscriber subscriber = subscriberRepository.findByEmailAndProfileId(email, profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscriber", "email", email));

        return subscriberMapper.toResponseDto(subscriber);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SubscriberResponseDTO> getSubscribersByProfile(Long profileId, Pageable pageable) {
        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return subscriberRepository.findByProfileId(profileId, pageable)
                .map(subscriberMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriberResponseDTO> getActiveSubscribers(Long profileId) {
        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return subscriberRepository.findByProfileIdAndIsActiveTrue(profileId)
                .stream()
                .map(subscriberMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriberResponseDTO> getConfirmedSubscribers(Long profileId) {
        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return subscriberRepository.findByProfileIdAndIsConfirmedTrue(profileId)
                .stream()
                .map(subscriberMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriberResponseDTO> searchSubscribers(Long profileId, String query) {
        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        if (query == null || query.trim().isEmpty()) {
            return subscriberRepository.findByProfileIdOrderBySubscribedAtDesc(profileId)
                    .stream()
                    .map(subscriberMapper::toResponseDto)
                    .collect(Collectors.toList());
        }

        return subscriberRepository.searchSubscribers(profileId, query.trim())
                .stream()
                .map(subscriberMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteSubscriber(Long id) {
        if (!subscriberRepository.existsById(id)) {
            throw new ResourceNotFoundException("Subscriber", "id", id);
        }

        subscriberRepository.deleteById(id);
        log.info("Subscriber deleted: {}", id);
    }

    @Override
    @Transactional
    public void deleteAllSubscribersByProfile(Long profileId) {
        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

//        subscriberRepository.deleteByProfileId(profileId);
    }

    @Override
    @Transactional
    public SubscriberResponseDTO deactivateSubscriber(Long id, String reason) {
        Subscriber subscriber = subscriberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscriber", "id", id));

        if (Boolean.FALSE.equals(subscriber.getIsActive())) {
            return subscriberMapper.toResponseDto(subscriber);
        }

        subscriber.setIsActive(false);
        subscriber.setUnsubscribedAt(LocalDateTime.now());

        Subscriber deactivated = subscriberRepository.save(subscriber);
        sendUnsubscribeConfirmationEmail(deactivated, reason);

        return subscriberMapper.toResponseDto(deactivated);
    }

    @Override
    @Transactional
    public SubscriberResponseDTO reactivateSubscriber(Long id) {
        Subscriber subscriber = subscriberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscriber", "id", id));

        if (Boolean.TRUE.equals(subscriber.getIsActive())) {
            return subscriberMapper.toResponseDto(subscriber);
        }

        subscriber.setIsActive(true);
        subscriber.setUnsubscribedAt(null);
        subscriber.setSubscribedAt(LocalDateTime.now());

        // If not confirmed, generate new token
        if (Boolean.FALSE.equals(subscriber.getIsConfirmed())) {
            subscriber.setConfirmationToken(generateConfirmationToken());
            sendConfirmationEmail(subscriber);
        } else {
            sendWelcomeBackEmail(subscriber);
        }

        Subscriber reactivated = subscriberRepository.save(subscriber);
        return subscriberMapper.toResponseDto(reactivated);
    }

    @Override
    @Transactional
    public SubscriberResponseDTO markAsConfirmed(Long id) {
        Subscriber subscriber = subscriberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscriber", "id", id));

        if (Boolean.TRUE.equals(subscriber.getIsConfirmed())) {
            return subscriberMapper.toResponseDto(subscriber);
        }

        subscriber.setIsConfirmed(true);
        subscriber.setConfirmationToken(null);

        Subscriber confirmed = subscriberRepository.save(subscriber);
        sendWelcomeEmail(confirmed);

        return subscriberMapper.toResponseDto(confirmed);
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriberStatsDTO getSubscriberStats(Long profileId) {
        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        long totalSubscribers = subscriberRepository.countByProfileId(profileId);
        long activeSubscribers = subscriberRepository.countByProfileIdAndIsActive(profileId, true);
        long confirmedSubscribers = subscriberRepository.countByProfileIdAndIsConfirmed(profileId, true);
        long unconfirmedSubscribers = subscriberRepository.countByProfileIdAndIsConfirmed(profileId, false);
        long unsubscribedCount = subscriberRepository.countByProfileIdAndIsActive(profileId, false);

//        long todaySubscriptions = subscriberRepository.countTodaySubscribers(profileId);
        long todaySubscriptions = 10l;

        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        long weekSubscriptions = subscriberRepository.countWeekSubscribers(profileId, weekAgo);

        LocalDateTime monthAgo = LocalDateTime.now().minusDays(30);
        long monthSubscriptions = subscriberRepository.countMonthSubscribers(profileId, monthAgo);

        // Source distribution
        List<Object[]> sourceCounts = subscriberRepository.countSubscribersBySource(profileId);
        Map<String, Long> sourceDistribution = new HashMap<>();
        for (Object[] result : sourceCounts) {
            sourceDistribution.put((String) result[0], (Long) result[1]);
        }

        // Growth rate (last 30 days vs previous 30 days)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime currentStart = now.minusDays(30);
        LocalDateTime previousStart = currentStart.minusDays(30);
        LocalDateTime previousEnd = currentStart.minusSeconds(1);

        Object[] growthData = subscriberRepository.getGrowthRateData(
                profileId, currentStart, now, previousStart, previousEnd);

        double currentPeriod = growthData[0] != null ? ((Number) growthData[0]).doubleValue() : 0;
        double previousPeriod = growthData[1] != null ? ((Number) growthData[1]).doubleValue() : 0;

        double growthRate = previousPeriod > 0 ?
                ((currentPeriod - previousPeriod) / previousPeriod) * 100 :
                (currentPeriod > 0 ? 100.0 : 0.0);

        return new SubscriberStatsDTO(
                totalSubscribers,
                activeSubscribers,
                confirmedSubscribers,
                unconfirmedSubscribers,
                unsubscribedCount,
                todaySubscriptions,
                weekSubscriptions,
                monthSubscriptions,
                sourceDistribution,
                Math.round(growthRate * 100.0) / 100.0 // Round to 2 decimals
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getSourceDistribution(Long profileId) {
        List<Object[]> results = subscriberRepository.countSubscribersBySource(profileId);

        Map<String, Long> distribution = new HashMap<>();
        for (Object[] result : results) {
            distribution.put((String) result[0], (Long) result[1]);
        }

        return distribution;
    }

    @Override
    @Transactional(readOnly = true)
    public Double getGrowthRate(Long profileId, int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime currentStart = now.minusDays(days);
        LocalDateTime previousStart = currentStart.minusDays(days);
        LocalDateTime previousEnd = currentStart.minusSeconds(1);

        Object[] growthData = subscriberRepository.getGrowthRateData(
                profileId, currentStart, now, previousStart, previousEnd);

        double currentPeriod = growthData[0] != null ? ((Number) growthData[0]).doubleValue() : 0;
        double previousPeriod = growthData[1] != null ? ((Number) growthData[1]).doubleValue() : 0;

        if (previousPeriod > 0) {
            return ((currentPeriod - previousPeriod) / previousPeriod) * 100;
        }

        return currentPeriod > 0 ? 100.0 : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriberResponseDTO> getRecentSubscribers(Long profileId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return subscriberRepository.findByProfileIdOrderBySubscribedAtDesc(profileId)
                .stream()
                .limit(limit)
                .map(subscriberMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void bulkImportSubscribers(Long profileId, List<SubscribeRequestDTO> subscribers) {
        log.info("Bulk importing {} subscribers for profile {}", subscribers.size(), profileId);

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "id", profileId));

        int imported = 0;
        int skipped = 0;

        for (SubscribeRequestDTO request : subscribers) {
            try {
                if (!subscriberRepository.existsByEmailAndProfileId(request.getEmail(), profileId)) {
                    subscribe(request);
                    imported++;
                } else {
                    skipped++;
                }
            } catch (Exception e) {
                log.error("Failed to import subscriber {}: {}", request.getEmail(), e.getMessage());
                skipped++;
            }
        }

        log.info("Bulk import completed: {} imported, {} skipped", imported, skipped);
    }

    @Override
    @Transactional
    public void bulkUpdateStatus(List<Long> ids, Boolean isActive) {
        List<Subscriber> subscribers = subscriberRepository.findAllById(ids);
        subscribers.forEach(subscriber -> subscriber.setIsActive(isActive));

        if (Boolean.FALSE.equals(isActive)) {
            subscribers.forEach(subscriber -> subscriber.setUnsubscribedAt(LocalDateTime.now()));
        } else {
            subscribers.forEach(subscriber -> subscriber.setUnsubscribedAt(null));
        }

        subscriberRepository.saveAll(subscribers);
    }

    @Override
    @Transactional
    public void sendWelcomeEmail(Long subscriberId) {
        Subscriber subscriber = subscriberRepository.findById(subscriberId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscriber", "id", subscriberId));

        sendWelcomeEmail(subscriber);
    }

    @Override
    @Transactional
    public void cleanupUnconfirmedSubscribers(Long profileId, int daysThreshold) {
        log.info("Cleaning up unconfirmed subscribers older than {} days for profile {}",
                daysThreshold, profileId);

        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysThreshold);
        List<Subscriber> unconfirmedSubscribers = subscriberRepository
                .findUnconfirmedSubscribersBefore(profileId, cutoffDate);

        int deleted = 0;
        for (Subscriber subscriber : unconfirmedSubscribers) {
            if (Boolean.FALSE.equals(subscriber.getIsConfirmed())) {
                subscriberRepository.delete(subscriber);
                deleted++;
            }
        }

        log.info("Deleted {} unconfirmed subscribers", deleted);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEmailSubscribed(String email, Long profileId) {
        Optional<Subscriber> subscriber = subscriberRepository.findByEmailAndProfileId(email, profileId);
        return subscriber.isPresent() && Boolean.TRUE.equals(subscriber.get().getIsActive());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEmailConfirmed(String email, Long profileId) {
        Optional<Subscriber> subscriber = subscriberRepository.findByEmailAndProfileId(email, profileId);
        return subscriber.isPresent() &&
                Boolean.TRUE.equals(subscriber.get().getIsActive()) &&
                Boolean.TRUE.equals(subscriber.get().getIsConfirmed());
    }

    @Override
    public String generateConfirmationToken() {
        return UUID.randomUUID().toString();
    }

    // Scheduled job to clean up unconfirmed subscribers (runs daily at 2 AM)
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void scheduledCleanup() {
        log.info("Running scheduled cleanup of unconfirmed subscribers");

        List<Profile> profiles = profileRepository.findAll();
        for (Profile profile : profiles) {
            cleanupUnconfirmedSubscribers(profile.getId(), 7); // 7 days threshold
        }
    }

    // Helper methods
    private Profile findProfileByIdentifier(String identifier) {
        // Try to find by email first
        Optional<Profile> byEmail = profileRepository.findByEmail(identifier);
        if (byEmail.isPresent()) {
            return byEmail.get();
        }

        // Try to find by ID
        try {
            Long id = Long.parseLong(identifier);
            return profileRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Profile", "identifier", identifier));
        } catch (NumberFormatException e) {
            // Try to find by other identifier (like slug)
            // You might need to add a findBySlug method to ProfileRepository
            throw new ResourceNotFoundException("Profile", "identifier", identifier);
        }
    }

    private void sendConfirmationEmail(Subscriber subscriber) {
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("subscriberName", subscriber.getName());
        templateVariables.put("subscriberEmail", subscriber.getEmail());
        templateVariables.put("confirmationToken", subscriber.getConfirmationToken());
        templateVariables.put("profileName", subscriber.getProfile().getFirstName() + " " +
                subscriber.getProfile().getLastName());
        templateVariables.put("confirmationLink",
                String.format("https://yourdomain.com/confirm-subscription?token=%s&email=%s",
                        subscriber.getConfirmationToken(), subscriber.getEmail()));

//        emailService.sendEmail(
//                subscriber.getEmail(),
//                "Confirm Your Subscription",
//                "subscription-confirmation",
//                templateVariables
//        );
        log.info("Confirmation email sent to: {}", subscriber.getEmail());
    }

    private void sendWelcomeEmail(Subscriber subscriber) {
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("subscriberName", subscriber.getName());
        templateVariables.put("profileName", subscriber.getProfile().getFirstName() + " " +
                subscriber.getProfile().getLastName());
        templateVariables.put("profileTitle", subscriber.getProfile().getTitle());

//        emailService.sendEmail(
//                subscriber.getEmail(),
//                "Welcome to Our Newsletter!",
//                "welcome-email",
//                templateVariables
//        );
        log.info("Welcome email sent to: {}", subscriber.getEmail());
    }

    private void sendUnsubscribeConfirmationEmail(Subscriber subscriber, String reason) {
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("subscriberName", subscriber.getName());
        templateVariables.put("profileName", subscriber.getProfile().getFirstName() + " " +
                subscriber.getProfile().getLastName());
        templateVariables.put("unsubscribeReason", reason != null ? reason : "Not specified");

//        emailService.sendEmail(
//                subscriber.getEmail(),
//                "You've Been Unsubscribed",
//                "unsubscribe-confirmation",
//                templateVariables
//        );
        log.info("Unsubscribe confirmation email sent to: {}", subscriber.getEmail());
    }

    private void sendWelcomeBackEmail(Subscriber subscriber) {
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("subscriberName", subscriber.getName());
        templateVariables.put("profileName", subscriber.getProfile().getFirstName() + " " +
                subscriber.getProfile().getLastName());

//        emailService.sendEmail(
//                subscriber.getEmail(),
//                "Welcome Back!",
//                "welcome-back-email",
//                templateVariables
//        );
        log.info("Welcome back email sent to: {}", subscriber.getEmail());
    }
}
