package com.mri.mri_backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "blocked_dates")
public class BlockedDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String institute;

    @Column(nullable = false)
    private LocalDate blockedDate;

    @Column(nullable = false)
    private String blockedBy;

    @Column(nullable = false)
    private LocalDateTime blockedAt;

    private String reason;

    // Constructors
    public BlockedDate() {
        this.blockedAt = LocalDateTime.now();
    }

    public BlockedDate(String institute, LocalDate blockedDate, String blockedBy) {
        this.institute = institute;
        this.blockedDate = blockedDate;
        this.blockedBy = blockedBy;
        this.blockedAt = LocalDateTime.now();
    }

    public BlockedDate(String institute, LocalDate blockedDate, String blockedBy, String reason) {
        this.institute = institute;
        this.blockedDate = blockedDate;
        this.blockedBy = blockedBy;
        this.reason = reason;
        this.blockedAt = LocalDateTime.now();
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