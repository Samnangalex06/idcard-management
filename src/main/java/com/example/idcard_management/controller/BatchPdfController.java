package com.example.idcard_management.controller;

import com.example.idcard_management.model.Profile;
import com.example.idcard_management.model.ProfileType;
import com.example.idcard_management.service.PdfService;
import com.example.idcard_management.service.ProfileService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/api/batch")
public class BatchPdfController {

    private final ProfileService profileService;
    private final PdfService pdfService;

    public BatchPdfController(ProfileService profileService, PdfService pdfService) {
        this.profileService = profileService;
        this.pdfService = pdfService;
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> generateBatchPdf(@RequestParam ProfileType type) throws Exception {
        List<Profile> profiles = profileService.getProfilesByType(type);

        ByteArrayOutputStream zipOutput = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(zipOutput);

        for (Profile profile : profiles) {
            byte[] pdf = pdfService.generateIdCardPdf(profile);

            String fileName = "id-card-" + profile.getRegistrationNumber() + ".pdf";

            ZipEntry entry = new ZipEntry(fileName);
            zip.putNextEntry(entry);
            zip.write(pdf);
            zip.closeEntry();
        }

        zip.close();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("batch-id-cards-" + type + ".zip")
                        .build()
        );

        return new ResponseEntity<>(zipOutput.toByteArray(), headers, HttpStatus.OK);
    }
}