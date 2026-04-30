package com.mri.mri_backend.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class FileConverterUtil {

    /**
     * Converts file to JSON with base64 encoding
     * Supports: JPG, JPEG, PNG, PDF
     */
    public Map<String, Object> convertJpgToJson(MultipartFile file) throws IOException {
        Map<String, Object> fileJson = new HashMap<>();

        // Validate file type
        if (!isValidFile(file)) {
            throw new IllegalArgumentException("Invalid file type. Only JPG, JPEG, PNG, PDF files are allowed.");
        }

        // Convert to base64
        String base64Content = Base64.getEncoder().encodeToString(file.getBytes());

        // Create JSON structure
        fileJson.put("fileName", file.getOriginalFilename());
        fileJson.put("fileSize", file.getSize());
        fileJson.put("contentType", file.getContentType());
        fileJson.put("base64Content", base64Content);
        fileJson.put("uploadTimestamp", System.currentTimeMillis());
        fileJson.put("status", "PENDING_APPROVAL");

        return fileJson;
    }

    /**
     * Validates if file is in supported format (JPG, JPEG, PNG, PDF)
     */
    private boolean isValidFile(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null) {
            return false;
        }
        return contentType.equals("image/jpeg") || 
               contentType.equals("image/jpg") || 
               contentType.equals("image/png") || 
               contentType.equals("application/pdf");
    }

    /**
     * Creates approval-ready JSON for multiple files
     */
    public Map<String, Object> createApprovalJson(
            MultipartFile aadhaarFile,
            MultipartFile employeeIdProof,
            MultipartFile nonMemberIdProof,
            MultipartFile paymentScreenshot,
            MultipartFile guarantorFile,
            MultipartFile ppoFile) throws IOException {

        Map<String, Object> approvalJson = new HashMap<>();

        // Convert each file to JSON
        if (aadhaarFile != null && !aadhaarFile.isEmpty()) {
            approvalJson.put("aadhaarCard", convertJpgToJson(aadhaarFile));
        }

        if (employeeIdProof != null && !employeeIdProof.isEmpty()) {
            approvalJson.put("employeeIdProof", convertJpgToJson(employeeIdProof));
        }

        if (nonMemberIdProof != null && !nonMemberIdProof.isEmpty()) {
            approvalJson.put("nonMemberIdProof", convertJpgToJson(nonMemberIdProof));
        }

        if (paymentScreenshot != null && !paymentScreenshot.isEmpty()) {
            approvalJson.put("paymentScreenshot", convertJpgToJson(paymentScreenshot));
        }

        if (guarantorFile != null && !guarantorFile.isEmpty()) {
            approvalJson.put("guarantorFile", convertJpgToJson(guarantorFile));
        }

        if (ppoFile != null && !ppoFile.isEmpty()) {
            approvalJson.put("ppoFile", convertJpgToJson(ppoFile));
        }

        // Add approval metadata
        approvalJson.put("totalFiles", approvalJson.size());
        approvalJson.put("approvalRequired", true);
        approvalJson.put("approvedBy", null);
        approvalJson.put("approvalTimestamp", null);

        return approvalJson;
    }

    /**
     * Approves a file (changes status to APPROVED)
     */
    public void approveFile(Map<String, Object> fileJson, String approvedBy) {
        fileJson.put("status", "APPROVED");
        fileJson.put("approvedBy", approvedBy);
        fileJson.put("approvalTimestamp", System.currentTimeMillis());
    }

    /**
     * Rejects a file (changes status to REJECTED)
     */
    public void rejectFile(Map<String, Object> fileJson, String rejectedBy, String reason) {
        fileJson.put("status", "REJECTED");
        fileJson.put("rejectedBy", rejectedBy);
        fileJson.put("rejectionReason", reason);
        fileJson.put("rejectionTimestamp", System.currentTimeMillis());
    }
}
