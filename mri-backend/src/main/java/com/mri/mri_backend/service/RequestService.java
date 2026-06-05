package com.mri.mri_backend.service;
import com.mri.mri_backend.Dto.RequestCreationDTO;
import com.mri.mri_backend.Dto.RequestDTO;
import com.mri.mri_backend.Dto.ApprovalRequestDTO;
import com.mri.mri_backend.Dto.ApprovalEntryDTO;
import com.mri.mri_backend.model.*;
import com.mri.mri_backend.repository.*;
import com.mri.mri_backend.util.FileConverterUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RequestService {

    private final RequestRepository requestRepository;
    private final EmailService emailService;
    private final FileConverterUtil fileConverterUtil;
    private final BlockedDateService blockedDateService;

    public RequestService(RequestRepository requestRepository,
                          EmailService emailService,
                          FileConverterUtil fileConverterUtil,
                          BlockedDateService blockedDateService) {

        this.requestRepository = requestRepository;
        this.emailService = emailService;
        this.fileConverterUtil = fileConverterUtil;
        this.blockedDateService = blockedDateService;
    }

    public RequestDTO createRequest(RequestCreationDTO requestCreationDTO) {

        try {
            System.out.println("RequestService.createRequest() - START");

            // Check if the booking date is blocked for the institute
            if (blockedDateService.isDateBlocked(requestCreationDTO.getInstitute(), requestCreationDTO.getBookingDate())) {
                throw new IllegalArgumentException("The selected booking date is blocked for this institute");
            }

            // Check if all booking dates in the range are available (for multi-day bookings)
            if (requestCreationDTO.getBookingEndDate() != null && 
                requestCreationDTO.getBookingEndDate().isAfter(requestCreationDTO.getBookingDate())) {
                
                List<BlockedDate> blockedInRange = blockedDateService.getBlockedDatesInRange(
                    requestCreationDTO.getInstitute(),
                    requestCreationDTO.getBookingDate(),
                    requestCreationDTO.getBookingEndDate()
                );

                if (!blockedInRange.isEmpty()) {
                    // One or more blocked date entities exist in the requested range
                    throw new IllegalArgumentException("Some dates in the booking range are blocked for this institute");
                }
                
                // Block all dates in the range for this booking
                blockedDateService.blockDateRange(
                    requestCreationDTO.getInstitute(),
                    requestCreationDTO.getBookingDate(),
                    requestCreationDTO.getBookingEndDate(),
                    "System - Booking Confirmed",
                    "Booking Request #" + System.currentTimeMillis()
                );
            } else {
                // Single-day booking - block that date
                blockedDateService.blockDate(
                    requestCreationDTO.getInstitute(),
                    requestCreationDTO.getBookingDate(),
                    "System - Booking Confirmed",
                    "Booking Request #" + System.currentTimeMillis()
                );
            }

            Request request = new Request();

            // Institute & Booking Info
            System.out.println("Setting institute and booking info...");
            request.setInstitute(requestCreationDTO.getInstitute());
            request.setBookingDate(requestCreationDTO.getBookingDate());
            request.setBookingEndDate(requestCreationDTO.getBookingEndDate());
            request.setPurpose(requestCreationDTO.getPurpose());
            request.setBookingCategory(requestCreationDTO.getBookingCategory());
            request.setGuests(requestCreationDTO.getGuests());
            request.setEventType(requestCreationDTO.getEventType());
            request.setEventDuration(requestCreationDTO.getEventDuration());

            // Personal Information
            System.out.println("Setting personal information...");
            request.setApplicantFirstName(requestCreationDTO.getFirstName());
            request.setApplicantLastName(requestCreationDTO.getLastName());
            request.setApplicantEmail(requestCreationDTO.getEmail());
            request.setApplicantPhone(requestCreationDTO.getPhone());
            request.setDesignation(requestCreationDTO.getDesignation());
            request.setDepartment(requestCreationDTO.getDepartment());

            // Address Information
            System.out.println("Setting address information...");
            request.setOrganization(requestCreationDTO.getOrganization());
            request.setStreetAddress(requestCreationDTO.getStreetAddress());
            request.setAddressLine2(requestCreationDTO.getAddressLine2());
            request.setCity(requestCreationDTO.getCity());
            request.setState(requestCreationDTO.getState());
            request.setZip(requestCreationDTO.getZip());
            request.setCountry(requestCreationDTO.getCountry());

            // Identity Information
            System.out.println("Setting identity information...");
            request.setPanNumber(requestCreationDTO.getPanNumber());
            request.setAadhaarNumber(requestCreationDTO.getAadhaarNumber());
            request.setAadhaarFileName(requestCreationDTO.getAadhaarFileUpload() != null ? requestCreationDTO.getAadhaarFileUpload().getOriginalFilename() : null);

            // Railway Employee Information
            System.out.println("Setting railway employee information...");
            request.setRailwayEmployeeId(requestCreationDTO.getRailwayEmployeeId());
            request.setRailwayEmployeeIdProofFileName(requestCreationDTO.getEmployeeIdProofUpload() != null ? requestCreationDTO.getEmployeeIdProofUpload().getOriginalFilename() : null);

            // Non-Member Employee Information
            System.out.println("Setting non-member employee information...");
            request.setNonMemberEmployeeId(requestCreationDTO.getNonMemberEmployeeId());
            request.setNonMemberEmployeeIdProofFileName(requestCreationDTO.getNonMemberIdProofUpload() != null ? requestCreationDTO.getNonMemberIdProofUpload().getOriginalFilename() : null);

            // Guarantor Information (for Non-Railway Person)
            System.out.println("Setting guarantor information...");
            request.setGuarantorName(requestCreationDTO.getGuarantorName());
            request.setGuarantorEmployeeId(requestCreationDTO.getGuarantorEmployeeId());
            request.setGuarantorPhone(requestCreationDTO.getGuarantorPhone());
            request.setGuarantorFileName(requestCreationDTO.getGuarantorFileUpload() != null ? requestCreationDTO.getGuarantorFileUpload().getOriginalFilename() : null);

            // PPO Information (for Retired Person)

            request.setPpoNumber(requestCreationDTO.getPpoNumber());
            request.setPpoFileName(requestCreationDTO.getPpoFileUpload() != null ? requestCreationDTO.getPpoFileUpload().getOriginalFilename() : null);

            // Payment Screenshot Information
            request.setPaymentScreenshotFileName(requestCreationDTO.getPaymentScreenshotUpload() != null ? 
                requestCreationDTO.getPaymentScreenshotUpload().getOriginalFilename() : null);



            // Facilities (default to "AC" if not provided)
            System.out.println("Setting facilities...");
            if (requestCreationDTO.getFacilities() != null && !requestCreationDTO.getFacilities().isEmpty()) {
                request.setFacilities(String.join(",", requestCreationDTO.getFacilities()));
            } else {
                // Default to AC if no facilities specified
                request.setFacilities("AC");
            }

            // Special Requirements
            System.out.println("Setting special requirements...");
            request.setSpecialRequirements(requestCreationDTO.getSpecialRequirements());

            // New payment fields
            System.out.println("Setting payment details...");
            request.setRefNo(requestCreationDTO.getRefNo());
            request.setAmountPaid(requestCreationDTO.getAmountPaid());

            // Bank account details
            System.out.println("Setting bank account details...");
            request.setAccountNo(requestCreationDTO.getAccountNo());
            request.setIfscCode(requestCreationDTO.getIfscCode());
            request.setBankName(requestCreationDTO.getBankName());

            // Handle file uploads and convert to JSON
            System.out.println("Processing file uploads...");
            try {
                Map<String, Object> fileApprovalJson = fileConverterUtil.createApprovalJson(
                    requestCreationDTO.getAadhaarFileUpload(),
                    requestCreationDTO.getEmployeeIdProofUpload(),
                    requestCreationDTO.getNonMemberIdProofUpload(),
                    requestCreationDTO.getPaymentScreenshotUpload(),
                    requestCreationDTO.getGuarantorFileUpload(),
                    requestCreationDTO.getPpoFileUpload()
                );
                System.out.println("✅ Files processed successfully. File keys: " + fileApprovalJson.keySet());
                request.setFileApprovalData(new ObjectMapper().writeValueAsString(fileApprovalJson));
            } catch (Exception e) {
                System.out.println("❌ Error processing files: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Error processing uploaded files: " + e.getMessage(), e);
            }

            // System Fields
            System.out.println("Setting system fields...");
            request.setRequestDate(requestCreationDTO.getRequestDate() != null ?
                                  requestCreationDTO.getRequestDate() : LocalDate.now());
            request.setApprovalStatus(ApprovalStatus.PENDING);
            request.setCurrentApprovalLevel(ApprovalLevel.OS);

            System.out.println("Saving request to database...");
            Request savedRequest = requestRepository.save(request);
            
            System.out.println("✅ Request saved successfully with ID: " + savedRequest.getRequestId());
            System.out.println("RequestService.createRequest() - SUCCESS");
            
            return convertToDTO(savedRequest);
            
        } catch (Exception e) {
            System.out.println("❌ RequestService.createRequest() - FAILED");
            System.out.println("Error: " + e.getMessage());
            System.out.println("Error Type: " + e.getClass().getName());
            e.printStackTrace();
            throw new RuntimeException("Error creating request: " + e.getMessage(), e);
        }
    }


    public List<RequestDTO> getAllRequests() {
        return requestRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public RequestDTO getRequestById(Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow(() -> 
            new IllegalArgumentException("Request not found with id: " + requestId));
        return convertToDTO(request);
    }

    // OS Level - Accept
    public RequestDTO approveByOS(Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow();

        if (request.getApprovalStatus() != ApprovalStatus.PENDING) {
            throw new IllegalArgumentException("Request is not in PENDING status. Current status: " + request.getApprovalStatus());
        }
        if (request.getCurrentApprovalLevel() != ApprovalLevel.OS) {
            throw new IllegalArgumentException("Request is not at OS level. Current level: " + request.getCurrentApprovalLevel());
        }

        request.setApprovalStatus(ApprovalStatus.OS_APPROVED);
        request.setCurrentApprovalLevel(ApprovalLevel.WI);

        Request savedRequest = requestRepository.save(request);
        return convertToDTO(savedRequest);
    }

    // OS Level - Reject
    public RequestDTO rejectByOS(Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow();

        if (request.getApprovalStatus() != ApprovalStatus.PENDING) {
            throw new IllegalArgumentException("Request is not in PENDING status. Current status: " + request.getApprovalStatus());
        }
        if (request.getCurrentApprovalLevel() != ApprovalLevel.OS) {
            throw new IllegalArgumentException("Request is not at OS level. Current level: " + request.getCurrentApprovalLevel());
        }

        request.setApprovalStatus(ApprovalStatus.REJECTED);
        Request savedRequest = requestRepository.save(request);

        emailService.sendRejectionEmail(request.getApplicantEmail(), request.getApplicantFirstName() + " " + request.getApplicantLastName());

        return convertToDTO(savedRequest);
    }

    // WI Level - Accept
    public RequestDTO approveByWI(Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow();

        if (request.getApprovalStatus() != ApprovalStatus.OS_APPROVED) {
            throw new IllegalArgumentException("Request must be OS_APPROVED first. Current status: " + request.getApprovalStatus());
        }
        if (request.getCurrentApprovalLevel() != ApprovalLevel.WI) {
            throw new IllegalArgumentException("Request is not at WI level. Current level: " + request.getCurrentApprovalLevel());
        }

        request.setApprovalStatus(ApprovalStatus.WI_APPROVED);
        request.setCurrentApprovalLevel(ApprovalLevel.DPO);

        Request savedRequest = requestRepository.save(request);
        return convertToDTO(savedRequest);
    }

    // WI Level - Reject
    public RequestDTO rejectByWI(Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow();

        if (request.getApprovalStatus() != ApprovalStatus.OS_APPROVED) {
            throw new IllegalArgumentException("Request must be OS_APPROVED first. Current status: " + request.getApprovalStatus());
        }
        if (request.getCurrentApprovalLevel() != ApprovalLevel.WI) {
            throw new IllegalArgumentException("Request is not at WI level. Current level: " + request.getCurrentApprovalLevel());
        }

        request.setApprovalStatus(ApprovalStatus.REJECTED);
        Request savedRequest = requestRepository.save(request);

        emailService.sendRejectionEmail(request.getApplicantEmail(), request.getApplicantFirstName() + " " + request.getApplicantLastName());

        return convertToDTO(savedRequest);
    }

    // DPO Level - Accept
    public RequestDTO approveByDPO(Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow();

        if (request.getApprovalStatus() != ApprovalStatus.WI_APPROVED) {
            throw new IllegalArgumentException("Request must be WI_APPROVED first. Current status: " + request.getApprovalStatus());
        }
        if (request.getCurrentApprovalLevel() != ApprovalLevel.DPO) {
            throw new IllegalArgumentException("Request is not at DPO level. Current level: " + request.getCurrentApprovalLevel());
        }

        request.setApprovalStatus(ApprovalStatus.DPO_APPROVED);
        request.setCurrentApprovalLevel(ApprovalLevel.SR_DPO);

        Request savedRequest = requestRepository.save(request);
        return convertToDTO(savedRequest);
    }

    // DPO Level - Reject
    public RequestDTO rejectByDPO(Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow();

        if (request.getApprovalStatus() != ApprovalStatus.WI_APPROVED) {
            throw new IllegalArgumentException("Request must be WI_APPROVED first. Current status: " + request.getApprovalStatus());
        }
        if (request.getCurrentApprovalLevel() != ApprovalLevel.DPO) {
            throw new IllegalArgumentException("Request is not at DPO level. Current level: " + request.getCurrentApprovalLevel());
        }

        request.setApprovalStatus(ApprovalStatus.REJECTED);
        Request savedRequest = requestRepository.save(request);

        emailService.sendRejectionEmail(request.getApplicantEmail(), request.getApplicantFirstName() + " " + request.getApplicantLastName());

        return convertToDTO(savedRequest);
    }

    // SR_DPO Level - Accept (Final Approval)
    public RequestDTO approveBySRDPO(Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow();

        if (request.getApprovalStatus() != ApprovalStatus.DPO_APPROVED) {
            throw new IllegalArgumentException("Request must be DPO_APPROVED first. Current status: " + request.getApprovalStatus());
        }
        if (request.getCurrentApprovalLevel() != ApprovalLevel.SR_DPO) {
            throw new IllegalArgumentException("Request is not at SR_DPO level. Current level: " + request.getCurrentApprovalLevel());
        }

        request.setApprovalStatus(ApprovalStatus.APPROVED);
        request.setCurrentApprovalLevel(ApprovalLevel.SR_DPO); // Keep track of final approval level
        Request savedRequest = requestRepository.save(request);

        // Send approval email to applicant
        // Send approval email to applicant
emailService.sendBookingApprovalEmail(
    request.getApplicantEmail(),
    request.getApplicantFirstName() + " " + request.getApplicantLastName(),
    request.getInstitute().toString(),
    request.getBookingDate().toString(),
    request.getPurpose(),
    request.getRequestId().toString(),
    request.getAadhaarNumber(),
    request.getBookingCategory(),
    request.getFacilities(),
    request.getSpecialRequirements(),
    Integer.parseInt(request.getEventType()), // 11th arg: Parsed to Integer
    request.getEventDuration(),
    request.getStartTime() != null ? request.getStartTime().toString() : "N/A",
    request.getEndTime() != null ? request.getEndTime().toString() : "N/A",
    request.getBookingEndDate() != null ? request.getBookingEndDate().toString() : "" // 15th arg: Passed safely
);

        return convertToDTO(savedRequest);
    }

    // Generic status update method for approvals with validation status and remark
    public RequestDTO updateBookingStatus(Long requestId, ApprovalRequestDTO approvalRequest) {
        Request request = requestRepository.findById(requestId).orElseThrow();

        if (approvalRequest.getApprovalEntry() == null || approvalRequest.getApprovalEntry().getRemark() == null || approvalRequest.getApprovalEntry().getRemark().trim().isEmpty()) {
            throw new IllegalArgumentException("Remark is required for approval or rejection.");
        }

        ApprovalEntryDTO entry = approvalRequest.getApprovalEntry();
        String role = approvalRequest.getRole();
        String status = approvalRequest.getStatus();

        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Role is required for approval.");
        }

        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status is required for approval.");
        }

        // Ensure validation status when needed
        if ((role.equalsIgnoreCase("os") || role.equalsIgnoreCase("wi") || role.equalsIgnoreCase("dpo")) &&
            (entry.getValidationStatus() == null || entry.getValidationStatus().trim().isEmpty())) {
            throw new IllegalArgumentException("Validation status is required for OS, WI, and DPO approvals.");
        }

        // Build or append approval history
        List<ApprovalEntryDTO> historyEntries = new ArrayList<>();
        if (request.getApprovalHistory() != null && !request.getApprovalHistory().isEmpty()) {
            try {
                historyEntries = new ObjectMapper().readValue(request.getApprovalHistory(),
                        new com.fasterxml.jackson.core.type.TypeReference<List<ApprovalEntryDTO>>() {});
            } catch (Exception e) {
                // If parsing fails, reset history to avoid corrupt state.
                historyEntries = new ArrayList<>();
            }
        }

        historyEntries.add(entry);
        try {
            request.setApprovalHistory(new ObjectMapper().writeValueAsString(historyEntries));
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize approval history: " + e.getMessage(), e);
        }

        // Update workflow status
        if (status.equalsIgnoreCase("rejected")) {
            request.setApprovalStatus(ApprovalStatus.REJECTED);
        } else if (status.equalsIgnoreCase("pending_wi") || status.equalsIgnoreCase("os_approved")) {
            request.setApprovalStatus(ApprovalStatus.OS_APPROVED);
            request.setCurrentApprovalLevel(ApprovalLevel.WI);
        } else if (status.equalsIgnoreCase("pending_dpo") || status.equalsIgnoreCase("wi_approved")) {
            request.setApprovalStatus(ApprovalStatus.WI_APPROVED);
            request.setCurrentApprovalLevel(ApprovalLevel.DPO);
        } else if (status.equalsIgnoreCase("pending_sr_dpo") || status.equalsIgnoreCase("dpo_approved")) {
            request.setApprovalStatus(ApprovalStatus.DPO_APPROVED);
            request.setCurrentApprovalLevel(ApprovalLevel.SR_DPO);
        } else if (status.equalsIgnoreCase("approved")) {
            request.setApprovalStatus(ApprovalStatus.APPROVED);
            request.setCurrentApprovalLevel(ApprovalLevel.SR_DPO);
            
            // Send approval email to applicant when final approval is granted
            emailService.sendBookingApprovalEmail(
                request.getApplicantEmail(),
                request.getApplicantFirstName() + " " + request.getApplicantLastName(),
                request.getInstitute().toString(),
                request.getBookingDate().toString(),
                request.getPurpose(),
                request.getRequestId().toString(),
                request.getAadhaarNumber(),
                request.getBookingCategory(),
                request.getFacilities(),
                request.getSpecialRequirements(),
                request.getEventType(),
                request.getEventDuration(),
                request.getStartTime() != null ? request.getStartTime().toString() : "N/A",
                request.getEndTime() != null ? request.getEndTime().toString() : "N/A",
                request.getBookingEndDate() != null ? request.getBookingEndDate().toString() : ""
            );
        } else {
            throw new IllegalArgumentException("Unknown status: " + status);
        }

        Request savedRequest = requestRepository.save(request);
        return convertToDTO(savedRequest);
    }

    // SR_DPO Level - Reject
    public RequestDTO rejectBySRDPO(Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow();

        if (request.getApprovalStatus() != ApprovalStatus.DPO_APPROVED) {
            throw new IllegalArgumentException("Request must be DPO_APPROVED first. Current status: " + request.getApprovalStatus());
        }
        if (request.getCurrentApprovalLevel() != ApprovalLevel.SR_DPO) {
            throw new IllegalArgumentException("Request is not at SR_DPO level. Current level: " + request.getCurrentApprovalLevel());
        }

        request.setApprovalStatus(ApprovalStatus.REJECTED);
        Request savedRequest = requestRepository.save(request);

        emailService.sendRejectionEmail(request.getApplicantEmail(), request.getApplicantFirstName() + " " + request.getApplicantLastName());

        return convertToDTO(savedRequest);
    }

    private RequestDTO convertToDTO(Request request) {
        RequestDTO dto = new RequestDTO();
        
        dto.setRequestId(request.getRequestId());

        // Institute & Booking Info
        dto.setInstitute(request.getInstitute());
        dto.setBookingDate(request.getBookingDate());
        dto.setBookingEndDate(request.getBookingEndDate());
        dto.setPurpose(request.getPurpose());
        dto.setBookingCategory(request.getBookingCategory());
        dto.setGuests(request.getGuests());
        dto.setEventType(request.getEventType());
        dto.setEventDuration(request.getEventDuration());

        // Personal Information
        dto.setApplicantFirstName(request.getApplicantFirstName());
        dto.setApplicantLastName(request.getApplicantLastName());
        dto.setApplicantEmail(request.getApplicantEmail());  // Single email field
        dto.setApplicantPhone(request.getApplicantPhone());
        dto.setDesignation(request.getDesignation());
        dto.setDepartment(request.getDepartment());

        // Address Information
        dto.setOrganization(request.getOrganization());
        dto.setStreetAddress(request.getStreetAddress());
        dto.setAddressLine2(request.getAddressLine2());
        dto.setCity(request.getCity());
        dto.setState(request.getState());
        dto.setZip(request.getZip());
        dto.setCountry(request.getCountry());

        // Identity Information
        dto.setPanNumber(request.getPanNumber());
        dto.setAadhaarNumber(request.getAadhaarNumber());
        dto.setAadhaarFileName(request.getAadhaarFileName());

        // Railway Employee Information
        dto.setRailwayEmployeeId(request.getRailwayEmployeeId());
        dto.setRailwayEmployeeIdProofFileName(request.getRailwayEmployeeIdProofFileName());

        // Non-Member Employee Information
        dto.setNonMemberEmployeeId(request.getNonMemberEmployeeId());
        dto.setNonMemberEmployeeIdProofFileName(request.getNonMemberEmployeeIdProofFileName());

        // PPO Information (for Retired Person)
        dto.setPpoNumber(request.getPpoNumber());
        dto.setPpoFileName(request.getPpoFileName());

        // New payment fields
        dto.setRefNo(request.getRefNo());
        dto.setAmountPaid(request.getAmountPaid());
        dto.setPaymentScreenshotFileName(request.getPaymentScreenshotFileName());

        // Bank Account Details
        dto.setAccountNo(request.getAccountNo());
        dto.setIfscCode(request.getIfscCode());
        dto.setBankName(request.getBankName());

        // Guarantor Information
        dto.setGuarantorName(request.getGuarantorName());
        dto.setGuarantorEmployeeId(request.getGuarantorEmployeeId());
        dto.setGuarantorPhone(request.getGuarantorPhone());
        dto.setGuarantorFileName(request.getGuarantorFileName());

        // Facilities
        dto.setFacilities(request.getFacilities());

        // Special Requirements
        dto.setSpecialRequirements(request.getSpecialRequirements());

        // Approval History (convert JSON string to list)
        if (request.getApprovalHistory() != null && !request.getApprovalHistory().isEmpty()) {
            try {
                dto.setApprovalHistory(new ObjectMapper().readValue(request.getApprovalHistory(),
                        new com.fasterxml.jackson.core.type.TypeReference<List<ApprovalEntryDTO>>() {}));
            } catch (Exception e) {
                dto.setApprovalHistory(new ArrayList<>());
            }
        } else {
            dto.setApprovalHistory(new ArrayList<>());
        }

        // File Approval Data
        dto.setFileApprovalData(request.getFileApprovalData());

        // System Fields
        dto.setRequestDate(request.getRequestDate());
        dto.setCreatedDate(request.getCreatedDate());
        dto.setUpdatedDate(request.getUpdatedDate());
        dto.setApprovalStatus(request.getApprovalStatus());
        dto.setCurrentApprovalLevel(request.getCurrentApprovalLevel());

        return dto;
    }

    public void sendBookingSubmissionNotification(Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow(() ->
            new IllegalArgumentException("Request not found with id: " + requestId));
        
        emailService.sendBookingSubmissionEmail(
            request.getApplicantEmail(),
            request.getApplicantFirstName() + " " + request.getApplicantLastName(),
            request.getRequestId().toString(),
            request.getInstitute(),
            request.getBookingDate().toString(),
            request.getBookingEndDate() != null ? request.getBookingEndDate().toString() : null,
            request.getPurpose()
        );
    }

    // Delete a request and unblock any blocked dates reserved for it
    public void deleteRequest(Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow(() ->
            new IllegalArgumentException("Request not found with id: " + requestId));

        // If booking dates were blocked for this request, remove those blocked dates
        try {
            if (request.getBookingDate() != null) {
                if (request.getBookingEndDate() != null && request.getBookingEndDate().isAfter(request.getBookingDate())) {
                    java.time.LocalDate current = request.getBookingDate();
                    while (!current.isAfter(request.getBookingEndDate())) {
                        blockedDateService.unblockDate(request.getInstitute(), current);
                        current = current.plusDays(1);
                    }
                } else {
                    blockedDateService.unblockDate(request.getInstitute(), request.getBookingDate());
                }
            }
        } catch (Exception ex) {
            // Log and continue with deletion
            System.err.println("Error while unblocking dates for request " + requestId + ": " + ex.getMessage());
        }

        requestRepository.deleteById(requestId);
    }

    // Revert booking back to previous approval level (for DPO and SR-DPO)
    public RequestDTO revertBookingStatus(Long requestId, ApprovalRequestDTO approvalRequest) {
        Request request = requestRepository.findById(requestId).orElseThrow(() ->
            new IllegalArgumentException("Request not found with id: " + requestId));

        if (approvalRequest.getApprovalEntry() == null || approvalRequest.getApprovalEntry().getRemark() == null || approvalRequest.getApprovalEntry().getRemark().trim().isEmpty()) {
            throw new IllegalArgumentException("Remark is required for revert.");
        }

        ApprovalEntryDTO entry = approvalRequest.getApprovalEntry();
        String role = approvalRequest.getRole();

        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Role is required for revert.");
        }

        // Build or append approval history
        List<ApprovalEntryDTO> historyEntries = new ArrayList<>();
        if (request.getApprovalHistory() != null && !request.getApprovalHistory().isEmpty()) {
            try {
                historyEntries = new ObjectMapper().readValue(request.getApprovalHistory(),
                        new com.fasterxml.jackson.core.type.TypeReference<List<ApprovalEntryDTO>>() {});
            } catch (Exception e) {
                historyEntries = new ArrayList<>();
            }
        }

        historyEntries.add(entry);
        try {
            request.setApprovalHistory(new ObjectMapper().writeValueAsString(historyEntries));
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize approval history: " + e.getMessage(), e);
        }

        // Revert based on current role and status
        if (role.equalsIgnoreCase("dpo")) {
            if (request.getApprovalStatus() != ApprovalStatus.WI_APPROVED) {
                throw new IllegalArgumentException("Can only revert DPO when status is WI_APPROVED. Current status: " + request.getApprovalStatus());
            }
            // Revert DPO → back to OS_APPROVED (WI level)
            request.setApprovalStatus(ApprovalStatus.OS_APPROVED);
            request.setCurrentApprovalLevel(ApprovalLevel.WI);
        } else if (role.equalsIgnoreCase("sr-dpo")) {
            if (request.getApprovalStatus() != ApprovalStatus.DPO_APPROVED) {
                throw new IllegalArgumentException("Can only revert SR-DPO when status is DPO_APPROVED. Current status: " + request.getApprovalStatus());
            }
            // Revert SR-DPO → back to WI_APPROVED (DPO level)
            request.setApprovalStatus(ApprovalStatus.WI_APPROVED);
            request.setCurrentApprovalLevel(ApprovalLevel.DPO);
        } else {
            throw new IllegalArgumentException("Revert is only allowed for DPO and SR-DPO roles. Current role: " + role);
        }

        Request savedRequest = requestRepository.save(request);
        return convertToDTO(savedRequest);
    }
}
