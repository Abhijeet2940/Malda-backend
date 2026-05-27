package com.mri.mri_backend.Dto;

import java.time.LocalDate;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

public class RequestCreationDTO {
    // Institute & Booking Info
    private String institute;
    private LocalDate bookingDate;
    private String purpose;
    private String bookingCategory;
    private Integer guests;
    private String eventType;
    private String eventDuration;
    private LocalDate bookingEndDate;
    private List<LocalDate> bookingDates;

    // Personal Information
    private String firstName;
    private String lastName;
    private String email;  // Single email field for applicant (from frontend form)
    private String phone;
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

    // Railway Employee Information
    private String railwayEmployeeId;
    private String employeeIdProof;

    // Non-Member Employee Information
    private String nonMemberEmployeeId;
    private String nonMemberEmployeeIdProof;

    // Facilities
    private List<String> facilities;

    // Special Requirements
    private String specialRequirements;

    // System Fields
    private String employeeEmail;
    private LocalDate requestDate;

    // File Uploads
    private MultipartFile aadhaarFileUpload;
    private MultipartFile employeeIdProofUpload;
    private MultipartFile nonMemberIdProofUpload;
    private MultipartFile paymentScreenshotUpload;
    private MultipartFile guarantorFileUpload;
    private MultipartFile ppoFileUpload;

    // Guarantor Information
    private String guarantorName;
    private String guarantorEmployeeId;
    private String guarantorPhone;

    // PPO Information
    private String ppoNumber;

    // New payment fields
    private String refNo;
    private String amountPaid;

    // Bank Account Details
    private String accountNo;
    private String ifscCode;
    private String bankName;

    // Constructors
    public RequestCreationDTO() {}

    // ...existing getters and setters...
    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
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

    public LocalDate getBookingEndDate() {
        return bookingEndDate;
    }

    public void setBookingEndDate(LocalDate bookingEndDate) {
        this.bookingEndDate = bookingEndDate;
    }

    public List<LocalDate> getBookingDates() {
        return bookingDates;
    }

    public void setBookingDates(List<LocalDate> bookingDates) {
        this.bookingDates = bookingDates;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getRailwayEmployeeId() {
        return railwayEmployeeId;
    }

    public void setRailwayEmployeeId(String railwayEmployeeId) {
        this.railwayEmployeeId = railwayEmployeeId;
    }

    public String getEmployeeIdProof() {
        return employeeIdProof;
    }

    public void setEmployeeIdProof(String employeeIdProof) {
        this.employeeIdProof = employeeIdProof;
    }

    public String getNonMemberEmployeeId() {
        return nonMemberEmployeeId;
    }

    public void setNonMemberEmployeeId(String nonMemberEmployeeId) {
        this.nonMemberEmployeeId = nonMemberEmployeeId;
    }

    public String getNonMemberEmployeeIdProof() {
        return nonMemberEmployeeIdProof;
    }

    public void setNonMemberEmployeeIdProof(String nonMemberEmployeeIdProof) {
        this.nonMemberEmployeeIdProof = nonMemberEmployeeIdProof;
    }

    public List<String> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<String> facilities) {
        this.facilities = facilities;
    }

    public String getSpecialRequirements() {
        return specialRequirements;
    }

    public void setSpecialRequirements(String specialRequirements) {
        this.specialRequirements = specialRequirements;
    }

    public String getEmployeeEmail() {
        return employeeEmail;
    }

    public void setEmployeeEmail(String employeeEmail) {
        this.employeeEmail = employeeEmail;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = requestDate;
    }

    public MultipartFile getAadhaarFileUpload() {
        return aadhaarFileUpload;
    }

    public void setAadhaarFileUpload(MultipartFile aadhaarFileUpload) {
        this.aadhaarFileUpload = aadhaarFileUpload;
    }

    public MultipartFile getEmployeeIdProofUpload() {
        return employeeIdProofUpload;
    }

    public void setEmployeeIdProofUpload(MultipartFile employeeIdProofUpload) {
        this.employeeIdProofUpload = employeeIdProofUpload;
    }

    public MultipartFile getNonMemberIdProofUpload() {
        return nonMemberIdProofUpload;
    }

    public void setNonMemberIdProofUpload(MultipartFile nonMemberIdProofUpload) {
        this.nonMemberIdProofUpload = nonMemberIdProofUpload;
    }

    public MultipartFile getPaymentScreenshotUpload() {
        return paymentScreenshotUpload;
    }

    public void setPaymentScreenshotUpload(MultipartFile paymentScreenshotUpload) {
        this.paymentScreenshotUpload = paymentScreenshotUpload;
    }

    public MultipartFile getGuarantorFileUpload() {
        return guarantorFileUpload;
    }

    public void setGuarantorFileUpload(MultipartFile guarantorFileUpload) {
        this.guarantorFileUpload = guarantorFileUpload;
    }

    public MultipartFile getPpoFileUpload() {
        return ppoFileUpload;
    }

    public void setPpoFileUpload(MultipartFile ppoFileUpload) {
        this.ppoFileUpload = ppoFileUpload;
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

    public String getPpoNumber() {
        return ppoNumber;
    }

    public void setPpoNumber(String ppoNumber) {
        this.ppoNumber = ppoNumber;
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
