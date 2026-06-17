package com.example.idcard_management.service;

import com.example.idcard_management.model.Profile;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PdfService {

    public byte[] generateIdCardPdf(Profile profile) throws Exception {
        String html = """
                <html>
                <head>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                        }
                        .card {
                            width: 500px;
                            border: 2px solid #1e3a8a;
                            border-radius: 16px;
                            padding: 24px;
                        }
                        .header {
                            background: #1e3a8a;
                            color: white;
                            padding: 16px;
                            border-radius: 12px;
                            text-align: center;
                        }
                        .name {
                            font-size: 24px;
                            font-weight: bold;
                            margin-top: 20px;
                        }
                        .info {
                            font-size: 14px;
                            margin-top: 10px;
                        }
                    </style>
                </head>
                <body>
                    <div class="card">
                        <div class="header">
                            <h2>ID CARD</h2>
                            <p>%s</p>
                        </div>
                        <div class="name">%s</div>
                        <div class="info">
                            <p><b>ID:</b> %s</p>
                            <p><b>Department:</b> %s</p>
                            <p><b>Title:</b> %s</p>
                            <p><b>Email:</b> %s</p>
                            <p><b>Phone:</b> %s</p>
                            <p><b>Blood Group:</b> %s</p>
                            <p><b>Issue Date:</b> %s</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(
                profile.getType(),
                profile.getFullName(),
                profile.getRegistrationNumber(),
                profile.getDepartment(),
                profile.getTitle(),
                profile.getEmail(),
                profile.getPhone(),
                profile.getBloodGroup(),
                profile.getIssueDate());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.withHtmlContent(html, null);
        builder.toStream(outputStream);
        builder.run();

        return outputStream.toByteArray();
    }
}