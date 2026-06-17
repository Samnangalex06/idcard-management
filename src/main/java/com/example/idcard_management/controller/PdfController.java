package com.example.idcard_management.controller;

import com.example.idcard_management.model.Profile;
import com.example.idcard_management.service.PdfService;
import com.example.idcard_management.service.ProfileService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    private final ProfileService profileService;
    private final PdfService pdfService;

    public PdfController(ProfileService profileService, PdfService pdfService) {
        this.profileService = profileService;
        this.pdfService = pdfService;
    }

    @GetMapping("/profiles/{id}")
    public ResponseEntity<byte[]> exportProfilePdf(@PathVariable Long id) throws Exception {
        Profile profile = profileService.getProfileById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        byte[] pdf = pdfService.generateIdCardPdf(profile);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("id-card-" + profile.getRegistrationNumber() + ".pdf")
                        .build());

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }
}