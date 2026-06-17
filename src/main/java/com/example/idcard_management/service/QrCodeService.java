package com.example.idcard_management.service;

import com.example.idcard_management.model.Profile;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class QrCodeService {

    public String generateQrCodeForProfile(Profile profile) throws Exception {
        Path qrDir = Path.of("uploads/qrcodes");

        if (!Files.exists(qrDir)) {
            Files.createDirectories(qrDir);
        }

        String fileName = profile.getRegistrationNumber() + "-qr.png";
        Path qrPath = qrDir.resolve(fileName);

        String qrText = buildQrText(profile);

        BitMatrix matrix = new MultiFormatWriter()
                .encode(qrText, BarcodeFormat.QR_CODE, 220, 220);

        MatrixToImageWriter.writeToPath(matrix, "PNG", qrPath);

        return "qrcodes/" + fileName;
    }

    private String buildQrText(Profile profile) {
        return "ID Card Verification\n"
                + "UUID: " + profile.getUuid() + "\n"
                + "Registration No: " + profile.getRegistrationNumber() + "\n"
                + "Name: " + profile.getFullName() + "\n"
                + "Type: " + profile.getType() + "\n"
                + "Department: " + profile.getDepartment() + "\n"
                + "Title: " + profile.getTitle() + "\n"
                + "Issue Date: " + profile.getIssueDate();
    }
}