package com.mri.mri_backend.service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.core.io.ClassPathResource;
import java.io.IOException;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private static final String FROM_EMAIL = "notifications@mri.indianrailways.gov.in";

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendApprovalEmail(String recipientEmail, String employeeName) {
        try {
            if (recipientEmail == null || recipientEmail.trim().isEmpty()) {
                System.err.println("Cannot send approval email: Recipient email is empty or null");
                return;
            }
            
            SimpleMailMessage message = new SimpleMailMessage();
            
            message.setFrom(FROM_EMAIL);
            message.setTo(recipientEmail);
            message.setSubject("Request Approved - Malda Railway Institute");
            message.setText(
                    "Dear " + employeeName +
                            ",\n\nYour request has been fully approved by Sr. DPO.\n\n" +
                            "Request Status: APPROVED\n\n" +
                            "Regards,\nMalda Railway Institute Administration\n\n" +
                            "This is an automated email. Please do not reply.");

            mailSender.send(message);
            System.out.println("✓ Approval email sent successfully to: " + recipientEmail);
        } catch (Exception e) {
            System.err.println("✗ Failed to send approval email to " + recipientEmail + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Async
    public void sendRejectionEmail(String recipientEmail, String employeeName) {
        try {
            if (recipientEmail == null || recipientEmail.trim().isEmpty()) {
                System.err.println("Cannot send rejection email: Recipient email is empty or null");
                return;
            }
            
            SimpleMailMessage message = new SimpleMailMessage();
            
            message.setFrom(FROM_EMAIL);
            message.setTo(recipientEmail);
            message.setSubject("Request Rejected - Malda Railway Institute");
            message.setText(
                    "Dear " + employeeName +
                            ",\n\nUnfortunately, your request has been rejected during the approval process.\n\n" +
                            "Request Status: REJECTED\n\n" +
                            "If you have any queries, please contact the administration.\n\n" +
                            "Regards,\nMalda Railway Institute Administration\n\n" +
                            "This is an automated email. Please do not reply.");

            mailSender.send(message);
            System.out.println("✓ Rejection email sent successfully to: " + recipientEmail);
        } catch (Exception e) {
            System.err.println("✗ Failed to send rejection email to " + recipientEmail + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Async
    public void sendBookingSubmissionEmail(
            String recipientEmail,
            String name,
            String bookingId,
            String instituteName,
            String bookingDate,
            String bookingEndDate,
            String purpose) {

        try {
            if (recipientEmail == null || recipientEmail.trim().isEmpty()) {
                System.err.println("Cannot send booking submission email: Recipient email is empty or null");
                return;
            }

            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(FROM_EMAIL);
            message.setTo(recipientEmail);
            message.setSubject("Booking Request Submitted - Malda Railway Institute");

            String dateRange = bookingEndDate != null && !bookingEndDate.isEmpty()
                ? String.format("%s to %s", bookingDate, bookingEndDate)
                : bookingDate;

            String emailBody = String.format(
                "Dear %s,\n\n" +
                "Your booking request has been successfully submitted for processing.\n\n" +
                "Submission Details:\n" +
                "• Booking ID: %s\n" +
                "• Institute: %s\n" +
                "• Date(s): %s\n" +
                "• Purpose: %s\n\n" +
                "Your request is now under review. You will receive further updates as the approval process progresses.\n\n" +
                "Important Notes:\n" +
                "• Please keep this submission ID for reference: %s\n" +
                "• You will be notified via email after final approval \n" +
                "• For any issues, contact the institute administration\n\n" +
                "Best regards,\n" +
                "Malda Division Railway Institute\n" +
                "Eastern Railway\n\n" +
                "***This is an automated email. Please do not reply.***",
                name, bookingId, instituteName, dateRange, purpose, bookingId
            );

            message.setText(emailBody);
            mailSender.send(message);

            System.out.println("✓ Booking submission email sent successfully to: " + recipientEmail);

        } catch (Exception e) {
            System.err.println("✗ Failed to send booking submission email to " + recipientEmail + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Helper method to get CC email based on institute
    private String getCCEmailForInstitute(String instituteName) {
        if (instituteName != null) {
            String institute = instituteName.toLowerCase().trim();
            if (institute.contains("malda")) {
                return "abhijeetmishra2940@gmail.com";
            } else if (institute.contains("bhagalpur")) {
                return "abhijeetkumar2940@gmail.com";
            }
        }
        return null;
    }

    @Async
    public void sendBookingApprovalEmail(
                String recipientEmail,
                String name,
                String instituteName,
                String bookingDate,
                String purpose,
                String entryId,
                String facilities,
                String specialRequirements,
                String eventType,
                String eventDuration,
                Integer guests,
                String startTime,
                String endTime,
                String bookingEndDate) {

        try {
            if (recipientEmail == null || recipientEmail.trim().isEmpty()) {
                System.err.println("Cannot send booking approval email: Recipient email is empty or null");
                return;
            }

            // Validate required parameters
            if (name == null || name.trim().isEmpty()) {
                System.err.println("Cannot send booking approval email: Name is empty or null");
                return;
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(FROM_EMAIL);
            helper.setTo(recipientEmail);
            
            // Add CC recipient based on institute
            String ccEmail = getCCEmailForInstitute(instituteName);
            if (ccEmail != null) {
                helper.setCc(ccEmail);
                System.out.println("✓ CC recipient added: " + ccEmail + " for institute: " + instituteName);
            }
            
            helper.setSubject("Booking Request Approved - Malda Railway Institute");

            // Enhanced email body with key details
            String emailBody = String.format(
                "Dear %s,\n\n" +
                "We are pleased to inform you that your booking request for Malda Railway Institute has been successfully approved.\n\n" +
                "Booking Details:\n" +
                "• Booking ID: %s\n" +
                "• Institute: %s\n" +
                "• Date: %s\n" +
                "• Purpose: %s\n" +
                "• Venue: %s\n\n" +
                "Please find attached the detailed booking confirmation PDF for your records.\n\n" +
                "Important Notes:\n" +
                "• Please arrive 15 minutes before the scheduled time\n" +
                "• Bring a valid ID proof for verification\n" +
                "• Contact the institute administration for any changes\n\n" +
                "Best regards,\n" +
                "Malda Division Railway Institute\n" +
                "Eastern Railway\n\n" +
                "This is an automated email. Please do not reply.",
                name, entryId, instituteName, bookingDate, purpose, facilities
            );

            helper.setText(emailBody);

            // ==================================================
            // PDF GENERATION ONLY
            // ==================================================
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);

            // ===============================================
        // OUTER BORDER (Premium Official Look)
            // ===============================================
            content.setLineWidth(1f);
            content.addRect(25, 25, 545, 742);
            content.stroke();

            float y = 770;

        // ===============================================
        // HEADER
        // ===============================================
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 24);
            content.newLineAtOffset(150, y);
            content.showText("EASTERN RAILWAY");
            content.endText();

            y -= 28;

            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 18);
            content.newLineAtOffset(135, y);
            content.showText("MALDA RAILWAY INSTITUTE");
            content.endText();

            y -= 22;

        // Header Line
            content.moveTo(40, y);
            content.lineTo(555, y);
            content.stroke();

            y -= 30;

        // ===============================================
        // TITLE
        // ===============================================
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 18);
            content.newLineAtOffset(145, y);
            content.showText("BOOKING CONFIRMATION");
            content.endText();

            y -= 20;

            // Title Line
            content.moveTo(40, y);
            content.lineTo(555, y);
            content.stroke();

            y -= 30;

        // ===============================================
        // DETAILS
        // ===============================================
            content.beginText();
            content.setFont(PDType1Font.TIMES_ROMAN, 16);
            content.setLeading(18f);
            content.newLineAtOffset(50, y);

            content.showText("Booking ID");
            content.newLineAtOffset(160, 0);
            content.showText(": " + entryId);
            content.newLineAtOffset(-160, -18);

            content.showText("Guest Name");
            content.newLineAtOffset(160, 0);
            content.showText(": " + name);
            content.newLineAtOffset(-160, -18);

            content.showText("Institute");
            content.newLineAtOffset(160, 0);
            String instituteDisplay = instituteName != null ? instituteName.toUpperCase() + " RAILWAY INSTITUTE" : "N/A";
            content.showText(": " + instituteDisplay);
            content.newLineAtOffset(-160, -18);

            // Format booking date(s) based on event duration
            String dateDisplay = bookingDate;
            if (eventDuration != null) {
                String duration = eventDuration.toLowerCase().trim();
                if (duration.contains("2") || duration.equals("2 day") || duration.equals("2days")) {
                    // Multi-day: show start date and end date
                    if (bookingEndDate != null && !bookingEndDate.trim().isEmpty()) {
                        dateDisplay = bookingDate + " to " + bookingEndDate;
                    }
                } else {
                    // Single day: show only booking date
                    dateDisplay = bookingDate;
                }
            }

            content.showText("Booking Date");
            content.newLineAtOffset(160, 0);
            content.showText(": " + dateDisplay);
            content.newLineAtOffset(-160, -18);

            content.showText("Purpose");
            content.newLineAtOffset(160, 0);
            content.showText(": " + purpose);
            content.newLineAtOffset(-160, -18);

            content.showText("Venue");
            content.newLineAtOffset(160, 0);
            content.showText(": " + facilities);
            content.newLineAtOffset(-160, -18);

            content.showText("Event Type");
            content.newLineAtOffset(160, 0);
            content.showText(": " + eventType);
            content.newLineAtOffset(-160, -18);

            content.showText("Event Duration");
            content.newLineAtOffset(160, 0);
            content.showText(": " + eventDuration);
            content.newLineAtOffset(-160, -18);

            content.showText("Guests");
            content.newLineAtOffset(160, 0);
            content.showText(": " + guests);
            content.newLineAtOffset(-160, -18);

            content.showText("Start Time");
            content.newLineAtOffset(160, 0);
            content.showText(": " + startTime);
            content.newLineAtOffset(-160, -18);

            content.showText("End Time");
            content.newLineAtOffset(160, 0);
            content.showText(": " + endTime);
            content.newLineAtOffset(-160, -18);

            content.showText("Special Requirements");
            content.newLineAtOffset(160, 0);
            content.showText(": " + (specialRequirements != null && !specialRequirements.trim().isEmpty() ? specialRequirements : "None"));

            content.endText();


        // ===============================================
        // NOTICE SECTION
        // ===============================================
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_OBLIQUE, 11);
            content.setLeading(14f);
            content.newLineAtOffset(145, 180);

            content.showText("System Generated Document");
            content.newLine();

            content.showText("No Physical Signature Required");
            content.newLine();

            content.showText("Bring This PDF for Entry and Verification");

            content.endText();

        // ===============================================
        // FOOTER LINE
        // ===============================================
            content.moveTo(40, 100);
            content.lineTo(555, 100);
            content.stroke();

            // ===============================================
            // FOOTER SIGNATURE SECTION WITH IMAGE
            // ===============================================
            float footerY = 78;

            // Load and embed signature image from resources
            try {
                ClassPathResource signatureResource = new ClassPathResource("Asset/SrDPO_signature.jpeg");
                if (signatureResource.exists()) {
                    PDImageXObject signatureImage = PDImageXObject.createFromFileByContent(
                            signatureResource.getFile(),
                            document
                    );
                    // Draw signature image on the left side (width: 80, height: 50)
                    content.drawImage(signatureImage, 50, footerY - 50, 80, 50);
                    
                    // Add designation below signature
                    content.beginText();
                    content.setFont(PDType1Font.HELVETICA, 10);
                    content.newLineAtOffset(50, footerY - 70);
                    content.endText();
                } else {
                    System.err.println("Signature image not found at Asset/SrDPO_signature.jpeg");
                    // Fallback to text if image not found
                    content.beginText();
                    content.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    content.newLineAtOffset(50, footerY);
                    content.showText("R. K. Sharma");
                    content.endText();
                    content.beginText();
                    content.setFont(PDType1Font.HELVETICA, 10);
                    content.newLineAtOffset(50, footerY - 16);
                    content.showText("Senior Divisional Personnel Officer");
                    content.endText();
                }
            } catch (Exception e) {
                System.err.println("Error loading signature image: " + e.getMessage());
                // Fallback to text if image loading fails
                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 12);
                content.newLineAtOffset(50, footerY);
                content.showText("R. K. Sharma");
                content.endText();
                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 10);
                content.newLineAtOffset(50, footerY - 16);
                content.showText("Senior Divisional Personnel Officer");
                content.endText();
            }

            // Right side Date Time
            content.beginText();
            content.setFont(PDType1Font.HELVETICA, 10);
            content.newLineAtOffset(360, footerY);
            content.showText(
                    "Date: " +
                            java.time.LocalDateTime.now()
                                    .format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a"))
            );
            content.endText();

            content.close();
            document.save(outputStream);
            document.close();


            ByteArrayDataSource dataSource =
                    new ByteArrayDataSource(outputStream.toByteArray(), "application/pdf");

            helper.addAttachment("Booking_Confirmation.pdf", dataSource);

            // ===============================================
            // SEND MAIL
            // ===============================================
            mailSender.send(message);

            System.out.println("✓ Booking approval email with PDF sent successfully to: " + recipientEmail);

        } catch (Exception e) {
            System.err.println("✗ Failed to send booking approval email to " + recipientEmail + ": " + e.getMessage());
            e.printStackTrace();
        }
    }}
