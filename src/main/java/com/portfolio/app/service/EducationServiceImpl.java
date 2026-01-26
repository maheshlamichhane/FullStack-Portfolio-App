package com.portfolio.app.service;

import com.portfolio.app.dao.EducationRepository;
import com.portfolio.app.dao.ProfileRepository;
import com.portfolio.app.dto.EducationDTO;
import com.portfolio.app.entity.Education;
import com.portfolio.app.entity.Profile;
import com.portfolio.app.exception.ResourceNotFoundException;
import com.portfolio.app.exception.BusinessRuleException;
import com.portfolio.app.mapper.EducationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EducationServiceImpl implements EducationService {

    private final EducationRepository educationRepository;
    private final ProfileRepository profileRepository;
    private final EducationMapper educationMapper;



    @Override
    @Transactional
    public EducationDTO createEducation(EducationDTO educationDTO) {
        log.info("Creating new education for profile {}: {} at {}",
                educationDTO.getProfileId(), educationDTO.getDegree(), educationDTO.getInstitution());

        Profile profile = profileRepository.findById(educationDTO.getProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "id", educationDTO.getProfileId()));

        validateEducationDates(educationDTO);

        Education education = educationMapper.toEntity(educationDTO);
        education.setProfile(profile);

        Education savedEducation = educationRepository.save(education);
        return educationMapper.toDto(savedEducation);
    }

    @Override
    @Transactional
    public EducationDTO updateEducation(Long id, EducationDTO educationDTO) {
        log.info("Updating education with ID: {}", id);

        Education existingEducation = educationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Education", "id", id));

        validateEducationDates(educationDTO);

        educationMapper.updateEntityFromDto(educationDTO, existingEducation);

        if (!existingEducation.getProfile().getId().equals(educationDTO.getProfileId())) {
            Profile profile = profileRepository.findById(educationDTO.getProfileId())
                    .orElseThrow(() -> new ResourceNotFoundException("Profile", "id", educationDTO.getProfileId()));
            existingEducation.setProfile(profile);
        }

        Education updatedEducation = educationRepository.save(existingEducation);
        return educationMapper.toDto(updatedEducation);
    }

    @Override
    @Transactional(readOnly = true)
    public EducationDTO getEducationById(Long id) {
        Education education = educationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Education", "id", id));
        return educationMapper.toDto(education);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EducationDTO> getAllEducationsByProfile(Long profileId) {
        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return educationRepository.findByProfileIdOrderByStartDateDesc(profileId)
                .stream()
                .map(educationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EducationDTO> getCurrentEducations(Long profileId) {
        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return educationRepository.findByProfileIdAndIsCurrentTrue(profileId)
                .stream()
                .map(educationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EducationDTO> searchEducationsByInstitution(Long profileId, String institution) {
        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return educationRepository.findByProfileIdAndInstitutionContainingIgnoreCase(profileId, institution)
                .stream()
                .map(educationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EducationDTO> searchEducationsByDegree(Long profileId, String degree) {
        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return educationRepository.findByProfileIdAndDegreeContainingIgnoreCase(profileId, degree)
                .stream()
                .map(educationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EducationDTO> getEducationsByFieldOfStudy(Long profileId, String fieldOfStudy) {
        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return educationRepository.findByProfileIdAndFieldOfStudy(profileId, fieldOfStudy)
                .stream()
                .map(educationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteEducation(Long id) {
        if (!educationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Education", "id", id);
        }
        educationRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteAllEducationsByProfile(Long profileId) {
        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }
//        educationRepository.deleteByProfileId(profileId);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getEducationStats(Long profileId) {
        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        List<Education> educations = educationRepository.findByProfileId(profileId);
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalEducations", educations.size());
        stats.put("currentEducations", educations.stream()
                .filter(e -> Boolean.TRUE.equals(e.getIsCurrent()))
                .count());
        stats.put("distinctInstitutions", educationRepository.findDistinctInstitutions(profileId).size());
        stats.put("highestDegree", educations.stream()
                .max(Comparator.comparing(Education::getStartDate))
                .map(Education::getDegree)
                .orElse(null));

        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getDistinctInstitutions(Long profileId) {
        return educationRepository.findDistinctInstitutions(profileId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getDistinctDegrees(Long profileId) {
        return educationRepository.findDistinctDegrees(profileId);
    }

    private void validateEducationDates(EducationDTO educationDTO) {
        if (educationDTO.getStartDate() == null) {
            throw new BusinessRuleException("Start date is required");
        }

        if (Boolean.FALSE.equals(educationDTO.getIsCurrent()) && educationDTO.getEndDate() == null) {
            throw new BusinessRuleException("End date is required for completed education");
        }

        if (educationDTO.getEndDate() != null && educationDTO.getStartDate().isAfter(educationDTO.getEndDate())) {
            throw new BusinessRuleException("Start date cannot be after end date");
        }

        if (educationDTO.getStartDate().isAfter(LocalDate.now())) {
            throw new BusinessRuleException("Start date cannot be in the future");
        }

        if (educationDTO.getEndDate() != null && educationDTO.getEndDate().isAfter(LocalDate.now())) {
            throw new BusinessRuleException("End date cannot be in the future");
        }
    }
}
