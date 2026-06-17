package com.example.idcard_management.service;

import com.example.idcard_management.model.Profile;
import com.example.idcard_management.model.ProfileType;
import com.example.idcard_management.repository.ProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }

    public Optional<Profile> getProfileById(Long id) {
        return profileRepository.findById(id);
    }

    public Optional<Profile> getProfileByUuid(String uuid) {
        return profileRepository.findByUuid(uuid);
    }

    public Optional<Profile> getProfileByRegistrationNumber(String registrationNumber) {
        return profileRepository.findByRegistrationNumber(registrationNumber);
    }

    public List<Profile> getProfilesByType(ProfileType type) {
        return profileRepository.findByType(type);
    }

    public Profile createProfile(Profile profile) {
        return profileRepository.save(profile);
    }

    public Profile updateProfile(Long id, Profile profileDetails) {
        return profileRepository.findById(id)
                .map(profile -> {
                    profile.setUuid(profileDetails.getUuid());
                    profile.setRegistrationNumber(profileDetails.getRegistrationNumber());
                    profile.setType(profileDetails.getType());
                    profile.setFullName(profileDetails.getFullName());
                    profile.setDepartment(profileDetails.getDepartment());
                    profile.setTitle(profileDetails.getTitle());
                    profile.setEmail(profileDetails.getEmail());
                    profile.setPhone(profileDetails.getPhone());
                    profile.setBloodGroup(profileDetails.getBloodGroup());
                    profile.setDateOfBirth(profileDetails.getDateOfBirth());
                    profile.setIssueDate(profileDetails.getIssueDate());
                    profile.setExpiryDate(profileDetails.getExpiryDate());
                    profile.setPhotoFileName(profileDetails.getPhotoFileName());
                    profile.setPhotoContentType(profileDetails.getPhotoContentType());
                    profile.setTemplate(profileDetails.getTemplate());
                    profile.setBarcodeType(profileDetails.getBarcodeType());
                    return profileRepository.save(profile);
                })
                .orElseThrow(() -> new RuntimeException("Profile not found with id: " + id));
    }

    public void deleteProfile(Long id) {
        profileRepository.deleteById(id);
    }

    public boolean existsByRegistrationNumber(String registrationNumber) {
        return profileRepository.existsByRegistrationNumber(registrationNumber);
    }
}
