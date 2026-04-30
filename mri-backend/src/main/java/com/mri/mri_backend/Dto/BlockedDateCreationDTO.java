package com.mri.mri_backend.Dto;

import java.time.LocalDate;

public class BlockedDateCreationDTO {
    private String institute;
    private LocalDate blockedDate;
    private String blockedBy;
    private String reason;

    // Constructors
    public BlockedDateCreationDTO() {}

    public BlockedDateCreationDTO(String institute, LocalDate blockedDate, String blockedBy, String reason) {
        this.institute = institute;
        this.blockedDate = blockedDate;
        this.blockedBy = blockedBy;
        this.reason = reason;
    }

    // Getters and Setters
    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public LocalDate getBlockedDate() {
        return blockedDate;
    }

    public void setBlockedDate(LocalDate blockedDate) {
        this.blockedDate = blockedDate;
    }

    public String getBlockedBy() {
        return blockedBy;
    }

    public void setBlockedBy(String blockedBy) {
        this.blockedBy = blockedBy;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}