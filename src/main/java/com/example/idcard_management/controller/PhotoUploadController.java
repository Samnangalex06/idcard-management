package com.example.idcard_management.controller;

import com.example.idcard_management.model.Profile;
import com.example.idcard_management.service.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/profiles")
public class PhotoUploadController {

    private final ProfileService profileService;
    private static final String UPLOAD_DIR = "uploads/photos";

    public PhotoUploadController(ProfileService profileService) {
        this.profileService = profileService;
        // Create upload directory if it doesn't exist
        new File(UPLOAD_DIR).mkdirs();
    }

    @PostMapping("/{id}/photo")
    public ResponseEntity<Profile> uploadPhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";
            String filename = UUID.randomUUID() + extension;

            // Save file to disk
            Path filePath = Paths.get(UPLOAD_DIR, filename);
            Files.write(filePath, file.getBytes());

            // Update profile with photo info
            Profile profile = profileService.getProfileById(id)
                    .orElseThrow(() -> new RuntimeException("Profile not found"));

            profile.setPhotoFileName(filename);
            profile.setPhotoContentType(file.getContentType());

            Profile updatedProfile = profileService.createProfile(profile);
            return ResponseEntity.ok(updatedProfile);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/photo")
    public ResponseEntity<byte[]> getPhoto(@PathVariable Long id) throws IOException {
        Profile profile = profileService.getProfileById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        if (profile.getPhotoFileName() == null) {
            return ResponseEntity.notFound().build();
        }

        Path filePath = Paths.get(UPLOAD_DIR, profile.getPhotoFileName());
        byte[] photoBytes = Files.readAllBytes(filePath);

        return ResponseEntity.ok()
                .header("Content-Type", profile.getPhotoContentType())
                .body(photoBytes);
    }

    @DeleteMapping("/{id}/photo")
    public ResponseEntity<Void> deletePhoto(@PathVariable Long id) throws IOException {
        Profile profile = profileService.getProfileById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        if (profile.getPhotoFileName() != null) {
            Path filePath = Paths.get(UPLOAD_DIR, profile.getPhotoFileName());
            Files.deleteIfExists(filePath);

            profile.setPhotoFileName(null);
            profile.setPhotoContentType(null);
            profileService.createProfile(profile);
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/qr-code")
    public ResponseEntity<byte[]> getQrCode(@PathVariable Long id) throws IOException {
        Profile profile = profileService.getProfileById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        if (profile.getQrCodeFileName() == null) {
            return ResponseEntity.notFound().build();
        }

        Path filePath = Paths.get("uploads/qrcodes", profile.getQrCodeFileName());
        byte[] qrBytes = Files.readAllBytes(filePath);

        return ResponseEntity.ok()
                .header("Content-Type", "image/png")
                .body(qrBytes);
    }
}
