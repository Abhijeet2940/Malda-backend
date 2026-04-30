package com.mri.mri_backend.Dto;

public class ApprovalRequestDTO {
    private String status;
    private ApprovalEntryDTO approvalEntry;
    private String role;

    // Constructors
    public ApprovalRequestDTO() {}

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ApprovalEntryDTO getApprovalEntry() {
        return approvalEntry;
    }

    public void setApprovalEntry(ApprovalEntryDTO approvalEntry) {
        this.approvalEntry = approvalEntry;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}