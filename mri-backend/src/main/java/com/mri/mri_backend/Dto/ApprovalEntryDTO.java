package com.mri.mri_backend.Dto;

public class ApprovalEntryDTO {
    private String role;
    private String action; // "approved" or "rejected"
    private String at;
    private String remark;
    private String validationStatus; // "valid" or "valid_with_issues" for OS/WI/DPO

    // Constructors
    public ApprovalEntryDTO() {}

    // Getters and Setters
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAt() {
        return at;
    }

    public void setAt(String at) {
        this.at = at;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus(String validationStatus) {
        this.validationStatus = validationStatus;
    }
}