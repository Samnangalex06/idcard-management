package com.example.idcard_management.controller;

import com.example.idcard_management.model.Profile;
import com.example.idcard_management.repository.ProfileRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PreviewController {

    private final ProfileRepository profileRepository;

    public PreviewController(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @GetMapping("/profiles/{id}/preview")
    public String previewProfile(@PathVariable Long id, Model model) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        model.addAttribute("profile", profile);

        return "id-card";
    }
}
