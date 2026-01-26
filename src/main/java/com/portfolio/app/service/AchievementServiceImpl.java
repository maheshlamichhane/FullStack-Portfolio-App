package com.portfolio.app.service;

import com.portfolio.app.dao.AchievementRepository;
import com.portfolio.app.dao.ProfileRepository;
import com.portfolio.app.dto.*;
import com.portfolio.app.entity.Achievement;
import com.portfolio.app.entity.Profile;
import com.portfolio.app.exception.ResourceNotFoundException;
import com.portfolio.app.exception.BusinessRuleException;
import com.portfolio.app.mapper.AchievementMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AchievementServiceImpl implements AchievementService {

    private final AchievementRepository achievementRepository;
    private final ProfileRepository profileRepository;
    private final AchievementMapper achievementMapper;

    @Override
    @Transactional
    public AchievementResponseDTO createAchievement(AchievementDTO achievementDTO) {
        log.info("Creating new achievement for profile {}: {}",
                achievementDTO.getProfileId(), achievementDTO.getTitle());

        // Validate profile exists
        Profile profile = profileRepository.findById(achievementDTO.getProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "id", achievementDTO.getProfileId()));

        // Validate achievement data
        validateAchievementData(achievementDTO);

        // Check for duplicate title (optional, but good for data quality)
        if (existsByTitleAndProfile(achievementDTO.getTitle(), achievementDTO.getProfileId())) {
            log.warn("Achievement with title '{}' already exists for profile {}",
                    achievementDTO.getTitle(), achievementDTO.getProfileId());
        }

        Achievement achievement = achievementMapper.toEntity(achievementDTO);
        achievement.setProfile(profile);

        Achievement savedAchievement = achievementRepository.save(achievement);
        log.info("Achievement created successfully with ID: {}", savedAchievement.getId());

        return achievementMapper.toResponseDto(savedAchievement);
    }

    @Override
    @Transactional(readOnly = true)
    public AchievementResponseDTO getAchievementById(Long id) {
        log.debug("Fetching achievement by ID: {}", id);

        Achievement achievement = achievementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Achievement", "id", id));

        return achievementMapper.toResponseDto(achievement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AchievementResponseDTO> getAllAchievementsByProfile(Long profileId) {
        log.debug("Fetching all achievements for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return achievementRepository.findByProfileIdOrderByDateReceivedDesc(profileId)
                .stream()
                .map(achievementMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AchievementResponseDTO> getAchievementsByProfile(Long profileId, Pageable pageable) {
        log.debug("Fetching achievements for profile ID: {} with pagination", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return achievementRepository.findByProfileId(profileId, pageable)
                .map(achievementMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AchievementResponseDTO> getAchievementsByCategory(Long profileId, String category) {
        log.debug("Fetching achievements in category '{}' for profile ID: {}", category, profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return achievementRepository.findByProfileIdAndCategory(profileId, category)
                .stream()
                .map(achievementMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AchievementResponseDTO> getFeaturedAchievements(Long profileId) {
        log.debug("Fetching featured achievements for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return achievementRepository.findByProfileIdAndIsFeaturedTrue(profileId)
                .stream()
                .map(achievementMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AchievementResponseDTO> getAchievementsByOrganization(Long profileId, String organization) {
        log.debug("Fetching achievements from organization '{}' for profile ID: {}", organization, profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return achievementRepository.findByProfileIdAndIssuingOrganization(profileId, organization)
                .stream()
                .map(achievementMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AchievementResponseDTO> getAchievementsByYear(Long profileId, Integer year) {
        log.debug("Fetching achievements from year {} for profile ID: {}", year, profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        if (year == null) {
            year = Year.now().getValue();
        }

        return achievementRepository.findByProfileIdAndYear(profileId, year)
                .stream()
                .map(achievementMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AchievementResponseDTO> getRecentAchievements(Long profileId, int limit) {
        log.debug("Fetching {} recent achievements for profile ID: {}", limit, profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        Pageable pageable = PageRequest.of(0, limit);
        return achievementRepository.findRecentAchievements(profileId, pageable)
                .stream()
                .map(achievementMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AchievementResponseDTO> searchAchievements(Long profileId, String query) {
        log.debug("Searching achievements with query '{}' for profile ID: {}", query, profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        if (query == null || query.trim().isEmpty()) {
            return getAllAchievementsByProfile(profileId);
        }

        return achievementRepository.searchAchievements(profileId, query.trim())
                .stream()
                .map(achievementMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AchievementCategoryDTO> getAchievementsGroupedByCategory(Long profileId) {
        log.debug("Grouping achievements by category for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        List<String> categories = achievementRepository.findDistinctCategories(profileId);
        List<AchievementCategoryDTO> result = new ArrayList<>();

        for (String category : categories) {
            List<Achievement> achievements = achievementRepository.findByProfileIdAndCategory(profileId, category);

            List<AchievementSummaryDTO> achievementDTOs = achievements.stream()
                    .map(achievementMapper::toSummaryDto)
                    .collect(Collectors.toList());

            AchievementCategoryDTO categoryDTO = new AchievementCategoryDTO(
                    category,
                    (long) achievements.size(),
                    achievementDTOs
            );

            result.add(categoryDTO);
        }

        // Sort categories by achievement count (descending)
        result.sort(Comparator.comparingLong(AchievementCategoryDTO::getCount).reversed());

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AchievementTimelineDTO> getAchievementsTimeline(Long profileId) {
        log.debug("Getting achievements timeline for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return achievementRepository.findAchievementsForTimeline(profileId)
                .stream()
                .map(achievementMapper::toTimelineDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AchievementResponseDTO updateAchievement(Long id, AchievementDTO achievementDTO) {
        log.info("Updating achievement with ID: {}", id);

        Achievement existingAchievement = achievementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Achievement", "id", id));

        // Validate achievement data
        validateAchievementData(achievementDTO);

        // Update profile if changed
        if (!existingAchievement.getProfile().getId().equals(achievementDTO.getProfileId())) {
            Profile profile = profileRepository.findById(achievementDTO.getProfileId())
                    .orElseThrow(() -> new ResourceNotFoundException("Profile", "id", achievementDTO.getProfileId()));
            existingAchievement.setProfile(profile);
        }

        achievementMapper.updateEntityFromDto(achievementDTO, existingAchievement);

        Achievement updatedAchievement = achievementRepository.save(existingAchievement);
        log.info("Achievement updated successfully: {}", id);

        return achievementMapper.toResponseDto(updatedAchievement);
    }

    @Override
    @Transactional
    public AchievementResponseDTO toggleFeatured(Long id) {
        log.info("Toggling featured status for achievement ID: {}", id);

        Achievement achievement = achievementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Achievement", "id", id));

        achievement.setIsFeatured(!Boolean.TRUE.equals(achievement.getIsFeatured()));

        Achievement updatedAchievement = achievementRepository.save(achievement);
        log.info("Achievement featured status toggled: {} is now {}", id, updatedAchievement.getIsFeatured());

        return achievementMapper.toResponseDto(updatedAchievement);
    }

    @Override
    @Transactional
    public void deleteAchievement(Long id) {
        log.info("Deleting achievement with ID: {}", id);

        if (!achievementRepository.existsById(id)) {
            throw new ResourceNotFoundException("Achievement", "id", id);
        }

        achievementRepository.deleteById(id);
        log.info("Achievement deleted: {}", id);
    }

    @Override
    @Transactional
    public void deleteAllAchievementsByProfile(Long profileId) {
        log.info("Deleting all achievements for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

//        achievementRepository.deleteByProfileId(profileId);
    }

    @Override
    @Transactional
    public void deleteAchievementsByCategory(Long profileId, String category) {
        log.info("Deleting achievements in category '{}' for profile ID: {}", category, profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        List<Achievement> achievements = achievementRepository.findByProfileIdAndCategory(profileId, category);
        achievementRepository.deleteAll(achievements);

        log.info("Deleted {} achievements in category '{}'", achievements.size(), category);
    }

    @Override
    @Transactional(readOnly = true)
    public AchievementStatsDTO getAchievementStats(Long profileId) {
        log.debug("Getting achievement statistics for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        List<Achievement> achievements = achievementRepository.findByProfileId(profileId);

        if (achievements.isEmpty()) {
            return new AchievementStatsDTO(
                    0L, 0L, Map.of(), 0, Map.of(), "None", "None"
            );
        }

        // Calculate basic statistics
        long totalAchievements = achievements.size();
        long featuredAchievements = achievements.stream()
                .filter(a -> Boolean.TRUE.equals(a.getIsFeatured()))
                .count();

        // Category distribution
        Map<String, Long> categoryDistribution = achievements.stream()
                .collect(Collectors.groupingBy(
                        Achievement::getCategory,
                        Collectors.counting()
                ));

        // Current year achievements
        long currentYearAchievements = achievementRepository.countCurrentYearAchievements(profileId);

        // Yearly achievements distribution
        List<Object[]> yearlyCounts = achievementRepository.countAchievementsByYear(profileId);
        Map<Integer, Long> yearlyAchievements = new TreeMap<>(Collections.reverseOrder());
        for (Object[] count : yearlyCounts) {
            yearlyAchievements.put((Integer) count[0], (Long) count[1]);
        }

        // Most recent achievement
        Achievement latest = achievementRepository.findLatestAchievement(profileId);
        String mostRecentAchievement = latest != null ? latest.getTitle() : "None";

        // Most frequent category
        String mostFrequentCategory = categoryDistribution.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("None");

        return new AchievementStatsDTO(
                totalAchievements,
                featuredAchievements,
                categoryDistribution,
                (int) currentYearAchievements,
                yearlyAchievements,
                mostRecentAchievement,
                mostFrequentCategory
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getCategoryDistribution(Long profileId) {
        List<Object[]> results = achievementRepository.countAchievementsByCategory(profileId);

        Map<String, Long> distribution = new HashMap<>();
        for (Object[] result : results) {
            distribution.put((String) result[0], (Long) result[1]);
        }

        return distribution;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Integer, Long> getYearlyDistribution(Long profileId) {
        List<Object[]> results = achievementRepository.countAchievementsByYear(profileId);

        Map<Integer, Long> distribution = new TreeMap<>(Collections.reverseOrder());
        for (Object[] result : results) {
            distribution.put((Integer) result[0], (Long) result[1]);
        }

        return distribution;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getDistinctCategories(Long profileId) {
        return achievementRepository.findDistinctCategories(profileId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getDistinctOrganizations(Long profileId) {
        return achievementRepository.findDistinctOrganizations(profileId);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getTotalAchievementYears(Long profileId) {
        List<Achievement> achievements = achievementRepository.findByProfileId(profileId);

        if (achievements.isEmpty()) {
            return 0;
        }

        Set<Integer> years = achievements.stream()
                .map(a -> a.getDateReceived().getYear())
                .collect(Collectors.toSet());

        return years.size();
    }

    @Override
    @Transactional(readOnly = true)
    public AchievementResponseDTO getLatestAchievement(Long profileId) {
        Achievement latest = achievementRepository.findLatestAchievement(profileId);

        if (latest == null) {
            throw new ResourceNotFoundException("Achievement", "profileId", profileId);
        }

        return achievementMapper.toResponseDto(latest);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByTitleAndProfile(String title, Long profileId) {
        List<Achievement> achievements = achievementRepository.findByProfileIdAndTitleContaining(profileId, title);
        return !achievements.isEmpty();
    }

    private void validateAchievementData(AchievementDTO achievementDTO) {
        if (achievementDTO.getDateReceived() == null) {
            throw new BusinessRuleException("Date received is required");
        }

        if (achievementDTO.getDateReceived().isAfter(LocalDate.now())) {
            throw new BusinessRuleException("Date received cannot be in the future");
        }

        if (achievementDTO.getCategory() == null || achievementDTO.getCategory().trim().isEmpty()) {
            throw new BusinessRuleException("Category is required");
        }
    }
}
