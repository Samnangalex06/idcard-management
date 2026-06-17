package com.example.idcard_management.controller;

import com.example.idcard_management.model.Profile;
import com.example.idcard_management.model.ProfileType;
import com.example.idcard_management.service.ProfileService;
import com.example.idcard_management.service.QrCodeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    private final ProfileService profileService;
    private final QrCodeService qrCodeService;

    public ProfileController(ProfileService profileService, QrCodeService qrCodeService) {
        this.profileService = profileService;
        this.qrCodeService = qrCodeService;
    }

    @GetMapping
    public ResponseEntity<List<Profile>> getAllProfiles() {
        return ResponseEntity.ok(profileService.getAllProfiles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Profile> getProfileById(@PathVariable Long id) {
        return profileService.getProfileById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/uuid/{uuid}")
    public ResponseEntity<Profile> getProfileByUuid(@PathVariable String uuid) {
        return profileService.getProfileByUuid(uuid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/registration-number/{registrationNumber}")
    public ResponseEntity<Profile> getProfileByRegistrationNumber(@PathVariable String registrationNumber) {
        return profileService.getProfileByRegistrationNumber(registrationNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Profile>> getProfilesByType(@PathVariable ProfileType type) {
        return ResponseEntity.ok(profileService.getProfilesByType(type));
    }

    @PostMapping
    public ResponseEntity<Profile> createProfile(@RequestBody Profile profile) {
        try {
            Profile savedProfile = profileService.createProfile(profile);

            String qrCodeFileName = qrCodeService.generateQrCodeForProfile(savedProfile);
            savedProfile.setQrCodeFileName(qrCodeFileName);

            Profile updatedProfile = profileService.updateProfile(savedProfile.getId(), savedProfile);

            return ResponseEntity.status(HttpStatus.CREATED).body(updatedProfile);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create profile with QR code: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Profile> updateProfile(@PathVariable Long id, @RequestBody Profile profile) {
        try {
            Profile updatedProfile = profileService.updateProfile(id, profile);
            return ResponseEntity.ok(updatedProfile);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/generate-qr")
    public ResponseEntity<Profile> generateQrCode(@PathVariable Long id) {
        try {
            Profile profile = profileService.getProfileById(id)
                    .orElseThrow(() -> new RuntimeException("Profile not found"));

            String qrCodeFileName = qrCodeService.generateQrCodeForProfile(profile);
            profile.setQrCodeFileName(qrCodeFileName);

            Profile updatedProfile = profileService.updateProfile(id, profile);

            return ResponseEntity.ok(updatedProfile);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR code: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long id) {
        if (profileService.getProfileById(id).isPresent()) {
            profileService.deleteProfile(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}