package com.portfolio.app.controller;

import com.portfolio.app.dto.EducationDTO;
import com.portfolio.app.service.EducationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/education")
@RequiredArgsConstructor
@Tag(name = "Education", description = "Education management APIs")
@Slf4j
public class EducationController {

    private final EducationService educationService;

    @PostMapping
    @Operation(summary = "Create a new education")
    public ResponseEntity<EducationDTO> createEducation(@Valid @RequestBody EducationDTO educationDTO) {
        EducationDTO createdEducation = educationService.createEducation(educationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEducation);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing education")
    public ResponseEntity<EducationDTO> updateEducation(
            @PathVariable Long id,
            @Valid @RequestBody EducationDTO educationDTO) {
        EducationDTO updatedEducation = educationService.updateEducation(id, educationDTO);
        return ResponseEntity.ok(updatedEducation);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get education by ID")
    public ResponseEntity<EducationDTO> getEducation(@PathVariable Long id) {
        EducationDTO education = educationService.getEducationById(id);
        return ResponseEntity.ok(education);
    }

    @GetMapping("/profile/{profileId}")
    @Operation(summary = "Get all educations for a profile")
    public ResponseEntity<List<EducationDTO>> getEducationsByProfile(@PathVariable Long profileId) {
        List<EducationDTO> educations = educationService.getAllEducationsByProfile(profileId);
        return ResponseEntity.ok(educations);
    }

    @GetMapping("/profile/{profileId}/current")
    @Operation(summary = "Get current educations")
    public ResponseEntity<List<EducationDTO>> getCurrentEducations(@PathVariable Long profileId) {
        List<EducationDTO> educations = educationService.getCurrentEducations(profileId);
        return ResponseEntity.ok(educations);
    }

    @GetMapping("/profile/{profileId}/institution")
    @Operation(summary = "Search educations by institution")
    public ResponseEntity<List<EducationDTO>> searchByInstitution(
            @PathVariable Long profileId,
            @RequestParam String institution) {
        List<EducationDTO> educations = educationService.searchEducationsByInstitution(profileId, institution);
        return ResponseEntity.ok(educations);
    }

    @GetMapping("/profile/{profileId}/degree")
    @Operation(summary = "Search educations by degree")
    public ResponseEntity<List<EducationDTO>> searchByDegree(
            @PathVariable Long profileId,
            @RequestParam String degree) {
        List<EducationDTO> educations = educationService.searchEducationsByDegree(profileId, degree);
        return ResponseEntity.ok(educations);
    }

    @GetMapping("/profile/{profileId}/field")
    @Operation(summary = "Get educations by field of study")
    public ResponseEntity<List<EducationDTO>> getByFieldOfStudy(
            @PathVariable Long profileId,
            @RequestParam String fieldOfStudy) {
        List<EducationDTO> educations = educationService.getEducationsByFieldOfStudy(profileId, fieldOfStudy);
        return ResponseEntity.ok(educations);
    }

    @GetMapping("/stats/{profileId}")
    @Operation(summary = "Get education statistics")
    public ResponseEntity<Map<String, Object>> getEducationStats(@PathVariable Long profileId) {
        Map<String, Object> stats = educationService.getEducationStats(profileId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/institutions/{profileId}")
    @Operation(summary = "Get distinct institutions")
    public ResponseEntity<List<String>> getDistinctInstitutions(@PathVariable Long profileId) {
        List<String> institutions = educationService.getDistinctInstitutions(profileId);
        return ResponseEntity.ok(institutions);
    }

    @GetMapping("/degrees/{profileId}")
    @Operation(summary = "Get distinct degrees")
    public ResponseEntity<List<String>> getDistinctDegrees(@PathVariable Long profileId) {
        List<String> degrees = educationService.getDistinctDegrees(profileId);
        return ResponseEntity.ok(degrees);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an education")
    public ResponseEntity<Void> deleteEducation(@PathVariable Long id) {
        educationService.deleteEducation(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/profile/{profileId}")
    @Operation(summary = "Delete all educations for a profile")
    public ResponseEntity<Void> deleteAllEducationsByProfile(@PathVariable Long profileId) {
        educationService.deleteAllEducationsByProfile(profileId);
        return ResponseEntity.noContent().build();
    }
}
