package com.mri.mri_backend.Dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BlockedDateDTO {
    private Long id;
    private String institute;
    private LocalDate blockedDate;
    private String blockedBy;
    private LocalDateTime blockedAt;
    private String reason;

    // Constructors
    public BlockedDateDTO() {}

    public BlockedDateDTO(Long id, String institute, LocalDate blockedDate, String blockedBy, LocalDateTime blockedAt, String reason) {
        this.id = id;
        this.institute = institute;
        this.blockedDate = blockedDate;
        this.blockedBy = blockedBy;
        this.blockedAt = blockedAt;
        this.reason = reason;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public LocalDateTime getBlockedAt() {
        return blockedAt;
    }

    public void setBlockedAt(LocalDateTime blockedAt) {
        this.blockedAt = blockedAt;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}