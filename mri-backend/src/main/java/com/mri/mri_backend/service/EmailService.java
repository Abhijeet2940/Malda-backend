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
                String aadhaarNumber,
                String bookingCategory,
                String facilities,
                String specialRequirements,
                String eventType,
                String eventDuration,
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
                "• Aadhaar Number: %s\n\n" +
                "Please find attached the detailed booking confirmation PDF for your records.\n\n" +
                "Important Notes:\n" +
                "• Please arrive 15 minutes before the scheduled time\n" +
                "• Bring a valid ID proof for verification\n" +
                "• Contact the institute administration for any changes\n\n" +
                "Best regards,\n" +
                "Malda Division Railway Institute\n" +
                "Eastern Railway\n\n" +
                "This is an automated email. Please do not reply.",
                name, entryId, instituteName, bookingDate, purpose, aadhaarNumber
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
        // HEADER - EASTERN RAILWAY (centered)
        // ===============================================
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 22);
            content.newLineAtOffset(200, y);
            content.showText("EASTERN RAILWAY");
            content.endText();

            y -= 28;

            // First divider line
            content.moveTo(40, y);
            content.lineTo(555, y);
            content.stroke();

            y -= 20;

        // ===============================================
        // HEADER - RAILWAY INSTITUTE, MALDA (centered)
        // ===============================================
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 22);
            content.newLineAtOffset(180, y);
            content.showText("RAILWAY INSTITUTE, MALDA");
            content.endText();

            y -= 22;

            // Second divider line
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
            content.setFont(PDType1Font.TIMES_ROMAN, 14);
            content.setLeading(18f);
            content.newLineAtOffset(48, y);

            content.showText("Booking ID");
            content.newLineAtOffset(170, 0);
            content.showText(": " + entryId);
            content.newLineAtOffset(-170, -20);

            content.showText("Guest Name");
            content.newLineAtOffset(170, 0);
            content.showText(": " + name);
            content.newLineAtOffset(-170, -20);

            content.showText("Institute");
            content.newLineAtOffset(170, 0);
            String instituteDisplay = instituteName != null ? instituteName.toUpperCase() + " RAILWAY INSTITUTE" : "N/A";
            content.showText(": " + instituteDisplay);
            content.newLineAtOffset(-170, -20);

            // Format booking date(s) based on event duration
            String dateDisplay = bookingDate;
            try {
                java.time.LocalDate startDate = java.time.LocalDate.parse(bookingDate);
                java.time.LocalDate endDate = (bookingEndDate != null && !bookingEndDate.trim().isEmpty()) ? java.time.LocalDate.parse(bookingEndDate) : startDate;

                if (eventDuration != null && eventDuration.toLowerCase().contains("2")) {
                    dateDisplay = startDate.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy")) + " to " + endDate.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy"));
                } else {
                    dateDisplay = startDate.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy"));
                }

                // Compute attractive start and end times: start = startDate 10:00 AM, end = (endDate + 1 day) 10:00 AM
                java.time.LocalDateTime startDateTime = startDate.atTime(10, 0);
                java.time.LocalDateTime endDateTime = endDate.plusDays(1).atTime(10, 0);
                java.time.format.DateTimeFormatter dtfLocal = java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
                startTime = startDateTime.format(dtfLocal);
                endTime = endDateTime.format(dtfLocal);
            } catch (Exception ex) {
                // Fallback to provided strings
            }

            content.showText("Booking Date");
            content.newLineAtOffset(170, 0);
            content.showText(": " + dateDisplay);
            content.newLineAtOffset(-170, -20);

            content.showText("Purpose");
            content.newLineAtOffset(170, 0);
            content.showText(": " + purpose);
            content.newLineAtOffset(-170, -20);

            content.showText("Aadhaar Number");
            content.newLineAtOffset(170, 0);
            content.showText(": " + (aadhaarNumber != null ? aadhaarNumber : "N/A"));
            content.newLineAtOffset(-170, -20);

            content.showText("Event Type");
            content.newLineAtOffset(170, 0);
            content.showText(": " + (eventType != null ? eventType : "N/A"));
            content.newLineAtOffset(-170, -20);

            content.showText("Event Duration");
            content.newLineAtOffset(170, 0);
            content.showText(": " + (eventDuration != null ? eventDuration + " DAY(S)" : "N/A"));
            content.newLineAtOffset(-170, -20);

            content.showText("Booking Category");
            content.newLineAtOffset(170, 0);
            content.showText(": " + (bookingCategory != null ? bookingCategory : "N/A"));
            content.newLineAtOffset(-170, -20);

            content.showText("Start Time");
            content.newLineAtOffset(170, 0);
            content.showText(": " + (startTime != null ? startTime : "N/A"));
            content.newLineAtOffset(-170, -20);

            content.showText("End Time");
            content.newLineAtOffset(170, 0);
            content.showText(": " + (endTime != null ? endTime : "N/A"));
            content.newLineAtOffset(-170, -20);

            content.showText("Special Requirements");
            content.newLineAtOffset(170, 0);
            content.showText(": " + (specialRequirements != null && !specialRequirements.trim().isEmpty() ? specialRequirements : "None"));
            content.endText();

            // ===============================================
            // HIGHLIGHT SECTION - 2 lines below Special Requirements
            // ===============================================
            float highlightY = y - (9 * 20) - 40; // Position 2 lines below special requirements
            
          content.beginText();
          content.setFont(PDType1Font.HELVETICA_BOLD, 12);
          content.newLineAtOffset(100, highlightY);
          content.showText("IMPORTANT: Please carry a valid ID proof along with this PDF for entry verification");
          content.endText();


            // ===============================================
            // FOOTER LINE
            // ===============================================
            content.moveTo(40, 100);
            content.lineTo(555, 100);
            content.stroke();

            // ===============================================
            // FOOTER SIGNATURE (placed above a divider on the right)
            // ===============================================
            float footerY = 118;

            // Load and embed signature image from resources and place it to the RIGHT
            try {
                ClassPathResource signatureResource = new ClassPathResource("Asset/SrDPO_signature.jpeg");
                if (signatureResource.exists()) {
                    PDImageXObject signatureImage = PDImageXObject.createFromFileByContent(
                            signatureResource.getFile(),
                            document
                    );
                    // Place signature on the right side
                    float sigWidth = 120f;
                    float sigHeight = 60f;
                    float sigX = 360f;
                    float sigY = footerY + 18f;
                    content.drawImage(signatureImage, sigX, sigY, sigWidth, sigHeight);
                } else {
                    System.err.println("Signature image not found at Asset/SrDPO_signature.jpeg");
                }
            } catch (Exception e) {
                System.err.println("Error loading signature image: " + e.getMessage());
            }

            // Divider line below signature
            content.moveTo(40, footerY - 30);
            content.lineTo(555, footerY - 30);
            content.stroke();

            // ===============================================
            // DATE (on the left below divider)
            // ===============================================
            java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");
            content.beginText();
            content.setFont(PDType1Font.HELVETICA, 10);
            content.newLineAtOffset(50, footerY - 50);
            content.showText("Date: " + java.time.LocalDateTime.now().format(dtf));
            content.endText();

            // ===============================================
            // NOTICE SECTION - Center Aligned on Last Line
            // ===============================================
            float noticeY = 28f;

            content.beginText();
            content.setFont(PDType1Font.HELVETICA_OBLIQUE, 10);
            content.newLineAtOffset(215, noticeY);
            content.showText("System Generated Document");
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
