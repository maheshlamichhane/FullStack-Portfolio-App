package com.portfolio.app.service;

import com.portfolio.app.dto.ProfileDTO;

import java.util.List;

public interface ProfileService {
    ProfileDTO createProfile(ProfileDTO profileDTO);
    ProfileDTO updateProfile(Long id, ProfileDTO profileDTO);
    ProfileDTO getProfileById(Long id);
    ProfileDTO getProfileByEmail(String email);
    List<ProfileDTO> getAllProfiles();
    void deleteProfile(Long id);
    boolean existsByEmail(String email);
}
