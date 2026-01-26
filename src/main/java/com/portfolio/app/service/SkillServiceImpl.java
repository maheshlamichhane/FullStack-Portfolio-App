package com.portfolio.app.service;


import com.portfolio.app.dao.ProfileRepository;
import com.portfolio.app.dao.SkillRepository;
import com.portfolio.app.dto.*;
import com.portfolio.app.entity.Skill;
import com.portfolio.app.entity.Profile;
import com.portfolio.app.exception.ResourceNotFoundException;
import com.portfolio.app.exception.BusinessRuleException;
import com.portfolio.app.mapper.SkillMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;
    private final ProfileRepository profileRepository;
    private final SkillMapper skillMapper;

    @Override
    @Transactional
    public SkillResponseDTO createSkill(SkillDTO skillDTO) {
        log.info("Creating new skill for profile {}: {}",
                skillDTO.getProfileId(), skillDTO.getName());

        // Validate profile exists
        Profile profile = profileRepository.findById(skillDTO.getProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "id", skillDTO.getProfileId()));

        // Check if skill with same name already exists for this profile
        if (skillRepository.existsByNameAndProfileId(skillDTO.getName(), skillDTO.getProfileId())) {
            throw new BusinessRuleException("Skill '" + skillDTO.getName() + "' already exists for this profile");
        }

        // Check if display order is available
        if (!isDisplayOrderAvailable(skillDTO.getProfileId(), skillDTO.getDisplayOrder(), null)) {
            throw new BusinessRuleException("Display order " + skillDTO.getDisplayOrder() + " is already taken");
        }

        Skill skill = skillMapper.toEntity(skillDTO);
        skill.setProfile(profile);

        Skill savedSkill = skillRepository.save(skill);
        log.info("Skill created successfully with ID: {}", savedSkill.getId());

        return skillMapper.toResponseDto(savedSkill);
    }

    @Override
    @Transactional(readOnly = true)
    public SkillResponseDTO getSkillById(Long id) {
        log.debug("Fetching skill by ID: {}", id);

        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill", "id", id));

        return skillMapper.toResponseDto(skill);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillResponseDTO> getAllSkillsByProfile(Long profileId) {
        log.debug("Fetching all skills for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return skillRepository.findByProfileId(profileId)
                .stream()
                .map(skillMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SkillResponseDTO> getSkillsByProfile(Long profileId, Pageable pageable) {
        log.debug("Fetching skills for profile ID: {} with pagination", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return skillRepository.findByProfileId(profileId, pageable)
                .map(skillMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillResponseDTO> getSkillsByCategory(Long profileId, String category) {
        log.debug("Fetching skills in category '{}' for profile ID: {}", category, profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return skillRepository.findByProfileIdAndCategory(profileId, category)
                .stream()
                .map(skillMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillResponseDTO> getFeaturedSkills(Long profileId) {
        log.debug("Fetching featured skills for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return skillRepository.findByProfileIdAndIsFeaturedTrue(profileId)
                .stream()
                .map(skillMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillResponseDTO> getSkillsByProficiencyRange(Long profileId, Integer min, Integer max) {
        log.debug("Fetching skills with proficiency between {} and {} for profile ID: {}", min, max, profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        if (min == null) min = 0;
        if (max == null) max = 100;

        return skillRepository.findByProfileIdAndProficiencyBetween(profileId, min, max)
                .stream()
                .map(skillMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillResponseDTO> getTopSkills(Long profileId, int limit) {
        log.debug("Fetching top {} skills for profile ID: {}", limit, profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        Pageable pageable = PageRequest.of(0, limit);
        return skillRepository.findTopSkills(profileId, pageable)
                .stream()
                .map(skillMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillResponseDTO> searchSkills(Long profileId, String query) {
        log.debug("Searching skills with query '{}' for profile ID: {}", query, profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        if (query == null || query.trim().isEmpty()) {
            return getAllSkillsByProfile(profileId);
        }

        return skillRepository.searchSkills(profileId, query.trim())
                .stream()
                .map(skillMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillSummaryDTO> getSkillsByDisplayOrder(Long profileId) {
        log.debug("Fetching skills by display order for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return skillRepository.findByProfileIdOrderByDisplayOrderAsc(profileId)
                .stream()
                .map(skillMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillCategoryDTO> getSkillsGroupedByCategory(Long profileId) {
        log.debug("Grouping skills by category for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        List<String> categories = skillRepository.findDistinctCategoriesByProfileId(profileId);
        List<SkillCategoryDTO> result = new ArrayList<>();

        for (String category : categories) {
            List<Skill> skills = skillRepository.findByProfileIdAndCategory(profileId, category);

            // Calculate average proficiency
            double avgProficiency = skills.stream()
                    .mapToInt(Skill::getProficiency)
                    .average()
                    .orElse(0.0);

            List<SkillSummaryDTO> skillDTOs = skills.stream()
                    .map(skillMapper::toSummaryDto)
                    .collect(Collectors.toList());

            SkillCategoryDTO categoryDTO = new SkillCategoryDTO(
                    category,
                    (long) skills.size(),
                    (int) Math.round(avgProficiency),
                    skillDTOs
            );

            result.add(categoryDTO);
        }

        // Sort categories by average proficiency (descending)
        result.sort(Comparator.comparingInt(SkillCategoryDTO::getAverageProficiency).reversed());

        return result;
    }

    @Override
    @Transactional
    public SkillResponseDTO updateSkill(Long id, SkillDTO skillDTO) {
        log.info("Updating skill with ID: {}", id);

        Skill existingSkill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill", "id", id));

        // Check if name is being changed and if it's available
        if (!existingSkill.getName().equals(skillDTO.getName())) {
            if (skillRepository.existsByNameAndProfileId(skillDTO.getName(), skillDTO.getProfileId())) {
                throw new BusinessRuleException("Skill '" + skillDTO.getName() + "' already exists for this profile");
            }
        }

        // Check if display order is being changed and if it's available
        if (!existingSkill.getDisplayOrder().equals(skillDTO.getDisplayOrder())) {
            if (!isDisplayOrderAvailable(skillDTO.getProfileId(), skillDTO.getDisplayOrder(), id)) {
                throw new BusinessRuleException("Display order " + skillDTO.getDisplayOrder() + " is already taken");
            }
        }

        // Update profile if changed
        if (!existingSkill.getProfile().getId().equals(skillDTO.getProfileId())) {
            Profile profile = profileRepository.findById(skillDTO.getProfileId())
                    .orElseThrow(() -> new ResourceNotFoundException("Profile", "id", skillDTO.getProfileId()));
            existingSkill.setProfile(profile);
        }

        skillMapper.updateEntityFromDto(skillDTO, existingSkill);

        Skill updatedSkill = skillRepository.save(existingSkill);
        log.info("Skill updated successfully: {}", id);

        return skillMapper.toResponseDto(updatedSkill);
    }

    @Override
    @Transactional
    public SkillResponseDTO toggleFeatured(Long id) {
        log.info("Toggling featured status for skill ID: {}", id);

        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill", "id", id));

        skill.setIsFeatured(!Boolean.TRUE.equals(skill.getIsFeatured()));

        Skill updatedSkill = skillRepository.save(skill);
        log.info("Skill featured status toggled: {} is now {}", id, updatedSkill.getIsFeatured());

        return skillMapper.toResponseDto(updatedSkill);
    }

    @Override
    @Transactional
    public SkillResponseDTO updateDisplayOrder(Long id, Integer displayOrder) {
        log.info("Updating display order for skill ID: {} to {}", id, displayOrder);

        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill", "id", id));

        // Check if display order is available
        if (!isDisplayOrderAvailable(skill.getProfile().getId(), displayOrder, id)) {
            throw new BusinessRuleException("Display order " + displayOrder + " is already taken");
        }

        skill.setDisplayOrder(displayOrder);
        Skill updatedSkill = skillRepository.save(skill);

        return skillMapper.toResponseDto(updatedSkill);
    }

    @Override
    @Transactional
    public void updateMultipleDisplayOrders(Map<Long, Integer> displayOrderUpdates) {
        log.info("Updating display orders for {} skills", displayOrderUpdates.size());

        List<Skill> skillsToUpdate = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : displayOrderUpdates.entrySet()) {
            Skill skill = skillRepository.findById(entry.getKey())
                    .orElseThrow(() -> new ResourceNotFoundException("Skill", "id", entry.getKey()));

            skill.setDisplayOrder(entry.getValue());
            skillsToUpdate.add(skill);
        }

        // Check for duplicate display orders within the batch
        Set<Integer> usedDisplayOrders = new HashSet<>();
        for (Skill skill : skillsToUpdate) {
            if (!usedDisplayOrders.add(skill.getDisplayOrder())) {
                throw new BusinessRuleException("Duplicate display order found: " + skill.getDisplayOrder());
            }
        }

        skillRepository.saveAll(skillsToUpdate);
    }

    @Override
    @Transactional
    public void deleteSkill(Long id) {
        log.info("Deleting skill with ID: {}", id);

        if (!skillRepository.existsById(id)) {
            throw new ResourceNotFoundException("Skill", "id", id);
        }

        skillRepository.deleteById(id);
        log.info("Skill deleted: {}", id);
    }

    @Override
    @Transactional
    public void deleteAllSkillsByProfile(Long profileId) {
        log.info("Deleting all skills for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

//        skillRepository.deleteByProfileId(profileId);
    }

    @Override
    @Transactional(readOnly = true)
    public SkillStatsDTO getSkillStats(Long profileId) {
        log.debug("Getting skill statistics for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        List<Skill> skills = skillRepository.findByProfileId(profileId);

        if (skills.isEmpty()) {
            return new SkillStatsDTO(0L, 0L, Map.of(), 0, 0, 0, 0.0);
        }

        // Calculate statistics
        long totalSkills = skills.size();
        long featuredSkills = skills.stream()
                .filter(s -> Boolean.TRUE.equals(s.getIsFeatured()))
                .count();

        // Category distribution
        Map<String, Long> categoryDistribution = skills.stream()
                .collect(Collectors.groupingBy(
                        Skill::getCategory,
                        Collectors.counting()
                ));

        // Proficiency stats
        int minProficiency = skills.stream()
                .mapToInt(Skill::getProficiency)
                .min()
                .orElse(0);
        int maxProficiency = skills.stream()
                .mapToInt(Skill::getProficiency)
                .max()
                .orElse(0);
        double avgProficiency = skills.stream()
                .mapToInt(Skill::getProficiency)
                .average()
                .orElse(0.0);

        // Average years of experience
        double avgExperience = skills.stream()
                .filter(s -> s.getYearsOfExperience() != null)
                .mapToInt(Skill::getYearsOfExperience)
                .average()
                .orElse(0.0);

        return new SkillStatsDTO(
                totalSkills,
                featuredSkills,
                categoryDistribution,
                (int) Math.round(avgProficiency),
                maxProficiency,
                minProficiency,
                Math.round(avgExperience * 100.0) / 100.0 // Round to 2 decimals
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Integer> getProficiencyDistribution(Long profileId) {
        log.debug("Getting proficiency distribution for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        List<Skill> skills = skillRepository.findByProfileId(profileId);

        Map<String, Integer> distribution = new LinkedHashMap<>();
        distribution.put("Beginner (1-25)", 0);
        distribution.put("Intermediate (26-50)", 0);
        distribution.put("Advanced (51-75)", 0);
        distribution.put("Expert (76-100)", 0);

        for (Skill skill : skills) {
            int proficiency = skill.getProficiency();
            if (proficiency <= 25) {
                distribution.put("Beginner (1-25)", distribution.get("Beginner (1-25)") + 1);
            } else if (proficiency <= 50) {
                distribution.put("Intermediate (26-50)", distribution.get("Intermediate (26-50)") + 1);
            } else if (proficiency <= 75) {
                distribution.put("Advanced (51-75)", distribution.get("Advanced (51-75)") + 1);
            } else {
                distribution.put("Expert (76-100)", distribution.get("Expert (76-100)") + 1);
            }
        }

        return distribution;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getSkillCategories(Long profileId) {
        log.debug("Getting skill categories for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return skillRepository.findDistinctCategoriesByProfileId(profileId);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getTotalYearsOfExperience(Long profileId) {
        Integer totalYears = skillRepository.getTotalYearsOfExperience(profileId);
        return totalYears != null ? totalYears : 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameAndProfile(String name, Long profileId) {
        return skillRepository.existsByNameAndProfileId(name, profileId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isDisplayOrderAvailable(Long profileId, Integer displayOrder, Long excludeId) {
        List<Skill> skills = skillRepository.findByProfileId(profileId);

        return skills.stream()
                .filter(skill -> !skill.getId().equals(excludeId))
                .noneMatch(skill -> displayOrder.equals(skill.getDisplayOrder()));
    }
}
