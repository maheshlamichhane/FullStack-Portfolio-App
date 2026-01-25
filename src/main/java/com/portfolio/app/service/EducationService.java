package com.portfolio.app.service;

import com.portfolio.app.dto.EducationDTO;
import java.util.List;
import java.util.Map;

public interface EducationService {
    EducationDTO createEducation(EducationDTO educationDTO);
    EducationDTO updateEducation(Long id, EducationDTO educationDTO);
    EducationDTO getEducationById(Long id);
    List<EducationDTO> getAllEducationsByProfile(Long profileId);
    List<EducationDTO> getCurrentEducations(Long profileId);
    List<EducationDTO> searchEducationsByInstitution(Long profileId, String institution);
    List<EducationDTO> searchEducationsByDegree(Long profileId, String degree);
    List<EducationDTO> getEducationsByFieldOfStudy(Long profileId, String fieldOfStudy);
    void deleteEducation(Long id);
    void deleteAllEducationsByProfile(Long profileId);
    Map<String, Object> getEducationStats(Long profileId);
    List<String> getDistinctInstitutions(Long profileId);
    List<String> getDistinctDegrees(Long profileId);
}
