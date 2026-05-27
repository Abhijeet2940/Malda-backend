package com.mri.mri_backend.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mri.mri_backend.Dto.ApprovalRequestDTO;
import com.mri.mri_backend.Dto.RequestCreationDTO;
import com.mri.mri_backend.Dto.RequestDTO;
import com.mri.mri_backend.service.RequestService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/requests")
    @CrossOrigin(origins = {"https://www.maldarailwayinstitute.in", "https://maldarailwayinstitute.in"})
public class RequestController {

    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RequestDTO createRequest(
            @RequestParam("employeeEmail") String employeeEmail,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam(value = "designation", required = false) String designation,
            @RequestParam(value = "department", required = false) String department,
            @RequestParam("organization") String organization,
            @RequestParam("streetAddress") String streetAddress,
            @RequestParam(value = "addressLine2", required = false) String addressLine2,
            @RequestParam("city") String city,
            @RequestParam("state") String state,
            @RequestParam("zip") String zip,
            @RequestParam("country") String country,
            @RequestParam(value = "panNumber", required = false) String panNumber,
            @RequestParam(value = "aadhaarNumber", required = false) String aadhaarNumber,
            @RequestParam("institute") String institute,
            @RequestParam("bookingDate") String bookingDate,
            @RequestParam("purpose") String purpose,
            @RequestParam("bookingCategory") String bookingCategory,
            @RequestParam("guests") Integer guests,
            @RequestParam(value = "railwayEmployeeId", required = false) String railwayEmployeeId,
            @RequestParam("eventType") String eventType,
            @RequestParam("eventDuration") String eventDuration,
            @RequestParam("facilities") String facilities,
            @RequestParam(value = "specialRequirements", required = false) String specialRequirements,
            @RequestParam("requestDate") String requestDate,
            @RequestParam(value = "aadhaarFile", required = false) MultipartFile aadhaarFile,
            @RequestParam(value = "employeeIdProof", required = false) MultipartFile employeeIdProof,
            @RequestParam(value = "nonMemberIdProof", required = false) MultipartFile nonMemberIdProof,
            @RequestParam(value = "paymentScreenshot", required = false) MultipartFile paymentScreenshot,
            @RequestParam(value = "guarantorFile", required = false) MultipartFile guarantorFile,
            @RequestParam(value = "ppoFile", required = false) MultipartFile ppoFile,
            @RequestParam(value = "guarantorName", required = false) String guarantorName,
            @RequestParam(value = "guarantorEmployeeId", required = false) String guarantorEmployeeId,
            @RequestParam(value = "guarantorPhone", required = false) String guarantorPhone,
            @RequestParam(value = "ppoNumber", required = false) String ppoNumber,
            @RequestParam(value = "bookingEndDate", required = false) String bookingEndDate,
            @RequestParam(value = "bookingDates", required = false) String bookingDates,
            @RequestParam("refNo") String refNo,
            @RequestParam("amountPaid") String amountPaid,
            @RequestParam("accountNo") String accountNo,
            @RequestParam("ifscCode") String ifscCode,
            @RequestParam("bankName") String bankName) {

        try {
            System.out.println("========== REQUEST SUBMISSION START ==========");
            
            // Create DTO and populate fields
            RequestCreationDTO dto = new RequestCreationDTO();
            dto.setEmployeeEmail(employeeEmail);
            dto.setFirstName(firstName);
            dto.setLastName(lastName);
            dto.setEmail(email);
            dto.setPhone(phone);
            dto.setDesignation(designation);
            dto.setDepartment(department);
            dto.setOrganization(organization);
            dto.setStreetAddress(streetAddress);
            dto.setAddressLine2(addressLine2);
            dto.setCity(city);
            dto.setState(state);
            dto.setZip(zip);
            dto.setCountry(country);
            dto.setPanNumber(panNumber);
            dto.setAadhaarNumber(aadhaarNumber);
            dto.setInstitute(institute);
            dto.setBookingDate(java.time.LocalDate.parse(bookingDate));
            dto.setPurpose(purpose);
            dto.setBookingCategory(bookingCategory);
            dto.setGuests(guests);
            dto.setRailwayEmployeeId(railwayEmployeeId);
            dto.setEventType(eventType);
            dto.setEventDuration(eventDuration);
            dto.setFacilities(java.util.Arrays.asList(facilities.split(",")));
            dto.setSpecialRequirements(specialRequirements);
            if (bookingEndDate != null && !bookingEndDate.isBlank()) {
                dto.setBookingEndDate(java.time.LocalDate.parse(bookingEndDate));
            }
            if (bookingDates != null && !bookingDates.isBlank()) {
                try {
                    java.util.List<String> rawDates = new ObjectMapper().readValue(bookingDates, new com.fasterxml.jackson.core.type.TypeReference<java.util.List<String>>() {});
                    java.util.List<java.time.LocalDate> parsedDates = new java.util.ArrayList<>();
                    for (String rawDate : rawDates) {
                        if (rawDate != null && !rawDate.isBlank()) {
                            parsedDates.add(java.time.LocalDate.parse(rawDate));
                        }
                    }
                    dto.setBookingDates(parsedDates);
                    if (!parsedDates.isEmpty() && dto.getBookingEndDate() == null) {
                        dto.setBookingEndDate(parsedDates.get(parsedDates.size() - 1));
                    }
                } catch (Exception e) {
                    System.err.println("Failed to parse bookingDates JSON: " + e.getMessage());
                }
            }
            dto.setRequestDate(java.time.LocalDate.parse(requestDate));

            System.out.println("✅ DTO fields populated successfully");

            // Set file uploads
            dto.setAadhaarFileUpload(aadhaarFile);
            dto.setEmployeeIdProofUpload(employeeIdProof);
            dto.setNonMemberIdProofUpload(nonMemberIdProof);
            dto.setPaymentScreenshotUpload(paymentScreenshot);
            dto.setGuarantorFileUpload(guarantorFile);
            dto.setPpoFileUpload(ppoFile);

            // Debug logging for file uploads
            System.out.println("=== FILE UPLOAD DEBUG ===");
            System.out.println("aadhaarFile: " + (aadhaarFile != null ? aadhaarFile.getOriginalFilename() + " (" + aadhaarFile.getSize() + " bytes)" : "null"));
            System.out.println("employeeIdProof: " + (employeeIdProof != null ? employeeIdProof.getOriginalFilename() + " (" + employeeIdProof.getSize() + " bytes)" : "null"));
            System.out.println("nonMemberIdProof: " + (nonMemberIdProof != null ? nonMemberIdProof.getOriginalFilename() + " (" + nonMemberIdProof.getSize() + " bytes)" : "null"));
            System.out.println("paymentScreenshot: " + (paymentScreenshot != null ? paymentScreenshot.getOriginalFilename() + " (" + paymentScreenshot.getSize() + " bytes)" : "null"));
            System.out.println("guarantorFile: " + (guarantorFile != null ? guarantorFile.getOriginalFilename() + " (" + guarantorFile.getSize() + " bytes)" : "null"));
            System.out.println("ppoFile: " + (ppoFile != null ? ppoFile.getOriginalFilename() + " (" + ppoFile.getSize() + " bytes)" : "null"));
            System.out.println("=========================");

            // Set guarantor details
            dto.setGuarantorName(guarantorName);
            dto.setGuarantorEmployeeId(guarantorEmployeeId);
            dto.setGuarantorPhone(guarantorPhone);
            dto.setPpoNumber(ppoNumber);
            dto.setRefNo(refNo);
            dto.setAmountPaid(amountPaid);
            dto.setAccountNo(accountNo);
            dto.setIfscCode(ifscCode);
            dto.setBankName(bankName);

            System.out.println("✅ All parameters parsed and set successfully");
            System.out.println("Calling requestService.createRequest()...");
            
            RequestDTO result = requestService.createRequest(dto);
            
            System.out.println("✅ Request created successfully with ID: " + result.getRequestId());
            System.out.println("========== REQUEST SUBMISSION SUCCESS ==========");
            
            return result;
            
        } catch (Exception e) {
            System.out.println("❌ ERROR DURING REQUEST SUBMISSION: " + e.getMessage());
            System.out.println("Error Type: " + e.getClass().getName());
            e.printStackTrace();
            System.out.println("========== REQUEST SUBMISSION FAILED ==========");
            throw new RuntimeException("Failed to submit booking request: " + e.getMessage(), e);
        }
    }

    @GetMapping
    public List<RequestDTO> getAllRequests() {
        return requestService.getAllRequests();
    }

    @PostMapping("/{id}/notify/submit")
    public ResponseEntity<String> notifySubmissionEmail(@PathVariable Long id) {
        try {
            requestService.sendBookingSubmissionNotification(id);
            return ResponseEntity.ok("Submission notification email sent successfully.");
        } catch (Exception e) {
            System.err.println("Failed to send submission notification: " + e.getMessage());
            return ResponseEntity.status(500).body("Failed to send submission notification email.");
        }
    }

    // OS Level
    @PutMapping("/{id}/approve/os")
    public RequestDTO approveByOS(@PathVariable Long id) {
        return requestService.approveByOS(id);
    }

    @PutMapping("/{id}/reject/os")
    public RequestDTO rejectByOS(@PathVariable Long id) {
        return requestService.rejectByOS(id);
    }

    // WI Level
    @PutMapping("/{id}/approve/wi")
    public RequestDTO approveByWI(@PathVariable Long id) {
        return requestService.approveByWI(id);
    }

    @PutMapping("/{id}/reject/wi")
    public RequestDTO rejectByWI(@PathVariable Long id) {
        return requestService.rejectByWI(id);
    }

    // DPO Level
    @PutMapping("/{id}/approve/dpo")
    public RequestDTO approveByDPO(@PathVariable Long id) {
        return requestService.approveByDPO(id);
    }

    @PutMapping("/{id}/reject/dpo")
    public RequestDTO rejectByDPO(@PathVariable Long id) {
        return requestService.rejectByDPO(id);
    }

    // SR DPO Level
    @PutMapping("/{id}/approve/sr-dpo")
    public RequestDTO approveBySRDPO(@PathVariable Long id) {
        return requestService.approveBySRDPO(id);
    }

    @PutMapping("/{id}/reject/sr-dpo")
    public RequestDTO rejectBySRDPO(@PathVariable Long id) {
        return requestService.rejectBySRDPO(id);
    }

    // Generic approval endpoint with remarks and validation status
    @PutMapping("/{id}/status")
    public RequestDTO updateBookingStatus(
            @PathVariable Long id,
            @RequestBody ApprovalRequestDTO approvalRequest) {
        return requestService.updateBookingStatus(id, approvalRequest);
    }


    // Health check endpoint for file download API
    @GetMapping("/health/download")
    public ResponseEntity<String> downloadHealthCheck() {
        return ResponseEntity.ok("✅ File download API is operational. Use: GET /api/requests/{id}/download/{fileKey}");
    }

    // Get all files for a request (preview)
    @GetMapping("/{id}/files")
    public ResponseEntity<Object> getRequestFiles(@PathVariable Long id) {
        try {
            RequestDTO request = requestService.getRequestById(id);
            
            if (request.getFileApprovalData() == null || request.getFileApprovalData().isEmpty()) {
                return ResponseEntity.ok().body("{\"message\": \"No files attached to this request\"}");
            }
            
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> fileApprovalData = objectMapper.readValue(
                request.getFileApprovalData(), 
                Map.class
            );
            
            // Return file list with metadata (without base64 content)
            Map<String, Object> filesList = new java.util.HashMap<>();
            for (String key : fileApprovalData.keySet()) {
                Map<String, Object> file = (Map<String, Object>) fileApprovalData.get(key);
                Map<String, Object> fileInfo = new java.util.HashMap<>();
                fileInfo.put("fileName", file.get("fileName"));
                fileInfo.put("fileSize", file.get("fileSize"));
                fileInfo.put("contentType", file.get("contentType"));
                fileInfo.put("status", file.get("status"));
                fileInfo.put("downloadUrl", "/api/requests/" + id + "/download/" + key);
                filesList.put(key, fileInfo);
            }
            
            return ResponseEntity.ok(filesList);
        } catch (Exception e) {
            System.err.println("Error fetching files for request " + id + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    // Download uploaded file endpoint
    @GetMapping("/{id}/download/{fileKey}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id, @PathVariable String fileKey) {
        try {
            RequestDTO request = requestService.getRequestById(id);
            
            // Parse the fileApprovalData JSON
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> fileApprovalData = objectMapper.readValue(
                request.getFileApprovalData(), 
                Map.class
            );
            
            // Get the specific file
            if (!fileApprovalData.containsKey(fileKey)) {
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> fileData = (Map<String, Object>) fileApprovalData.get(fileKey);
            String base64Content = (String) fileData.get("base64Content");
            String fileName = (String) fileData.get("fileName");
            String contentType = (String) fileData.get("contentType");
            
            // Decode base64
            byte[] fileBytes = Base64.getDecoder().decode(base64Content);
            
            // Return file with appropriate headers
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, contentType != null ? contentType : "application/octet-stream")
                    .body(fileBytes);
                    
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // Delete a request (and cleanup associated blocked dates)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRequest(@PathVariable Long id) {
        try {
            requestService.deleteRequest(id);
            return ResponseEntity.ok("Request deleted successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Failed to delete request " + id + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to delete request: " + e.getMessage());
        }
    }
}
