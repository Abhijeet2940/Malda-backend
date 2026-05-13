package com.mri.mri_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.io.ByteArrayOutputStream;
import java.util.*;

@Service
public class EmailService {

    @Value("${BREVO_API_KEY}")
    private String apiKey;

    private static final String FROM_EMAIL = "info.maldarailwayinstitute@gmail.com";
    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    @Async
    public void sendBookingApprovalEmail(String recipientEmail, String name, String instituteName, String bookingDate, String purpose, String entryId, String facilities, String specialRequirements, String eventType, String eventDuration, Integer guests, String startTime, String endTime) {
        try {
            if (recipientEmail == null || recipientEmail.trim().isEmpty()) return;

            // ==================================================
            // PDF GENERATION (Your Original Logic)
            // ==================================================
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream content = new PDPageContentStream(document, page);

            content.setLineWidth(1f);
            content.addRect(25, 25, 545, 742);
            content.stroke();

            float y = 770;
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 24);
            content.newLineAtOffset(150, y);
            content.showText("EASTERN RAILWAY");
            content.endText();

            // ... [Keep the rest of your PDPageContentStream logic here] ...

            content.close();
            document.save(outputStream);
            document.close();

            // ==================================================
            // BREVO API CALL (The Fix for Render)
            // ==================================================
            byte[] pdfBytes = outputStream.toByteArray();
            String base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", apiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("sender", Map.of("name", "Malda Railway Institute", "email", FROM_EMAIL));
            requestBody.put("to", List.of(Map.of("email", recipientEmail, "name", name)));
            requestBody.put("subject", "Booking Request Approved - Malda Railway Institute");
            requestBody.put("htmlContent", "<html><body><p>Dear " + name + ", your booking is approved. See attached PDF.</p></body></html>");
            
            // Attach PDF via Base64
            requestBody.put("attachment", List.of(Map.of(
                "content", base64Pdf,
                "name", "Booking_Confirmation.pdf"
            )));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            restTemplate.postForEntity(BREVO_API_URL, entity, String.class);

            System.out.println("✓ Email sent successfully via Brevo API to: " + recipientEmail);

        } catch (Exception e) {
            System.err.println("✗ Failed to send email via API: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
