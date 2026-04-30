package com.mri.mri_backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "requests")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;


    // Basic Request Information
    private String requestTitle;
    private String requestDescription;

    // Booking Details
    @Enumerated(EnumType.STRING)
    private FacilityType facilityType;

    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer durationHours;

    // Institute & Booking Info
    private String institute;
    private String purpose;
    private String bookingCategory;
    private Integer guests;
    private String eventType;
    private String eventDuration;

    // Personal Information
    private String applicantFirstName;
    private String applicantLastName;
    private String applicantEmail;  // Single email field for applicant
    private String applicantPhone;
    private String designation;
    private String department;

    // Address Information
    private String organization;
    private String streetAddress;
    private String addressLine2;
    private String city;
    private String state;
    private String zip;
    private String country;

    // Identity Information
    private String panNumber;
    private String aadhaarNumber;
    private String aadhaarFileName;

    // Railway Employee Information
    private String railwayEmployeeId;
    private String railwayEmployeeIdProofFileName;

    // Non-Member Employee Information
    private String nonMemberEmployeeId;
    private String nonMemberEmployeeIdProofFileName;

    // Guarantor Information (for Non-Railway Person)
    private String guarantorName;
    private String guarantorEmployeeId;
    private String guarantorPhone;
    private String guarantorFileName;

    // PPO Information (for Retired Person)
    private String ppoNumber;
    private String ppoFileName;

    // Facilities (stored as comma-separated string)
    @Column(columnDefinition = "TEXT")
    private String facilities;

    // Booking Requirements
    private String specialRequirements;

    // File Approval Data (JSON string)
    @Column(columnDefinition = "TEXT")
    private String fileApprovalData;

    // System Fields
    private LocalDate requestDate;
    private LocalDate createdDate;
    private LocalDate updatedDate;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;

    @Enumerated(EnumType.STRING)
    private ApprovalLevel currentApprovalLevel;

    @Column
    private String paymentScreenshotFileName;

    // New payment fields
    private String refNo;
    private String amountPaid;

    // Bank Account Details
    private String accountNo;
    private String ifscCode;
    private String bankName;

    // Approval History (stored as JSON string)
    @Column(columnDefinition = "TEXT")
    private String approvalHistory;

    // Constructors
    public Request() {
        this.createdDate = LocalDate.now();
        this.updatedDate = LocalDate.now();
    }

    // Getters and Setters
    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }


    public String getRequestTitle() {
        return requestTitle;
    }

    public void setRequestTitle(String requestTitle) {
        this.requestTitle = requestTitle;
    }

    public String getRequestDescription() {
        return requestDescription;
    }

    public void setRequestDescription(String requestDescription) {
        this.requestDescription = requestDescription;
    }

    public FacilityType getFacilityType() {
        return facilityType;
    }

    public void setFacilityType(FacilityType facilityType) {
        this.facilityType = facilityType;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Integer getDurationHours() {
        return durationHours;
    }

    public void setDurationHours(Integer durationHours) {
        this.durationHours = durationHours;
    }


    public String getPaymentScreenshotFileName() {
        return paymentScreenshotFileName;
    }

    public void setPaymentScreenshotFileName(String paymentScreenshotFileName) {
        this.paymentScreenshotFileName = paymentScreenshotFileName;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    public String getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(String amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getApprovalHistory() {
        return approvalHistory;
    }

    public void setApprovalHistory(String approvalHistory) {
        this.approvalHistory = approvalHistory;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getBookingCategory() {
        return bookingCategory;
    }

    public void setBookingCategory(String bookingCategory) {
        this.bookingCategory = bookingCategory;
    }

    public Integer getGuests() {
        return guests;
    }

    public void setGuests(Integer guests) {
        this.guests = guests;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventDuration() {
        return eventDuration;
    }

    public void setEventDuration(String eventDuration) {
        this.eventDuration = eventDuration;
    }

    public String getApplicantFirstName() {
        return applicantFirstName;
    }

    public void setApplicantFirstName(String applicantFirstName) {
        this.applicantFirstName = applicantFirstName;
    }

    public String getApplicantLastName() {
        return applicantLastName;
    }

    public void setApplicantLastName(String applicantLastName) {
        this.applicantLastName = applicantLastName;
    }

    public String getApplicantEmail() {
        return applicantEmail;
    }

    public void setApplicantEmail(String applicantEmail) {
        this.applicantEmail = applicantEmail;
    }

    public String getApplicantPhone() {
        return applicantPhone;
    }

    public void setApplicantPhone(String applicantPhone) {
        this.applicantPhone = applicantPhone;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public String getAadhaarNumber() {
        return aadhaarNumber;
    }

    public void setAadhaarNumber(String aadhaarNumber) {
        this.aadhaarNumber = aadhaarNumber;
    }

    public String getAadhaarFileName() {
        return aadhaarFileName;
    }

    public void setAadhaarFileName(String aadhaarFileName) {
        this.aadhaarFileName = aadhaarFileName;
    }

    public String getRailwayEmployeeId() {
        return railwayEmployeeId;
    }

    public void setRailwayEmployeeId(String railwayEmployeeId) {
        this.railwayEmployeeId = railwayEmployeeId;
    }

    public String getRailwayEmployeeIdProofFileName() {
        return railwayEmployeeIdProofFileName;
    }

    public void setRailwayEmployeeIdProofFileName(String railwayEmployeeIdProofFileName) {
        this.railwayEmployeeIdProofFileName = railwayEmployeeIdProofFileName;
    }

    public String getNonMemberEmployeeId() {
        return nonMemberEmployeeId;
    }

    public void setNonMemberEmployeeId(String nonMemberEmployeeId) {
        this.nonMemberEmployeeId = nonMemberEmployeeId;
    }

    public String getNonMemberEmployeeIdProofFileName() {
        return nonMemberEmployeeIdProofFileName;
    }

    public void setNonMemberEmployeeIdProofFileName(String nonMemberEmployeeIdProofFileName) {
        this.nonMemberEmployeeIdProofFileName = nonMemberEmployeeIdProofFileName;
    }

    public String getGuarantorName() {
        return guarantorName;
    }

    public void setGuarantorName(String guarantorName) {
        this.guarantorName = guarantorName;
    }

    public String getGuarantorEmployeeId() {
        return guarantorEmployeeId;
    }

    public void setGuarantorEmployeeId(String guarantorEmployeeId) {
        this.guarantorEmployeeId = guarantorEmployeeId;
    }

    public String getGuarantorPhone() {
        return guarantorPhone;
    }

    public void setGuarantorPhone(String guarantorPhone) {
        this.guarantorPhone = guarantorPhone;
    }

    public String getGuarantorFileName() {
        return guarantorFileName;
    }

    public void setGuarantorFileName(String guarantorFileName) {
        this.guarantorFileName = guarantorFileName;
    }

    public String getPpoNumber() {
        return ppoNumber;
    }

    public void setPpoNumber(String ppoNumber) {
        this.ppoNumber = ppoNumber;
    }

    public String getPpoFileName() {
        return ppoFileName;
    }

    public void setPpoFileName(String ppoFileName) {
        this.ppoFileName = ppoFileName;
    }

    public String getFacilities() {
        return facilities;
    }

    public void setFacilities(String facilities) {
        this.facilities = facilities;
    }

    public String getSpecialRequirements() {
        return specialRequirements;
    }

    public void setSpecialRequirements(String specialRequirements) {
        this.specialRequirements = specialRequirements;
    }

    public String getFileApprovalData() {
        return fileApprovalData;
    }

    public void setFileApprovalData(String fileApprovalData) {
        this.fileApprovalData = fileApprovalData;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = requestDate;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDate getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDate updatedDate) {
        this.updatedDate = updatedDate;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public ApprovalLevel getCurrentApprovalLevel() {
        return currentApprovalLevel;
    }

    public void setCurrentApprovalLevel(ApprovalLevel currentApprovalLevel) {
        this.currentApprovalLevel = currentApprovalLevel;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
