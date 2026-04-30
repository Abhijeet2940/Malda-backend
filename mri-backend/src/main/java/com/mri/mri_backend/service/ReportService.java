package com.mri.mri_backend.service;

import com.mri.mri_backend.model.ApprovalStatus;
import com.mri.mri_backend.model.Request;
import com.mri.mri_backend.repository.RequestRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final RequestRepository requestRepository;

    public ReportService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    public Map<String, Object> getMonthlyReport(String month) {
        Map<String, Object> report = new HashMap<>();

        try {
            // Parse month parameter (expected format: "2023-03")
            YearMonth yearMonth = YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyy-MM"));
            LocalDate startOfMonth = yearMonth.atDay(1);
            LocalDate endOfMonth = yearMonth.atEndOfMonth();

            List<Request> requests = requestRepository.findAll().stream()
                .filter(req -> req.getRequestDate() != null &&
                        !req.getRequestDate().isBefore(startOfMonth) &&
                        !req.getRequestDate().isAfter(endOfMonth))
                .toList();

            // Convert requests to reportData format
            List<Map<String, Object>> reportData = requests.stream()
                .map(this::convertRequestToReportData)
                .collect(Collectors.toList());

            // Calculate statistics
            long totalRequests = requests.size();
            long approvedRequests = requests.stream()
                .filter(req -> req.getApprovalStatus() == ApprovalStatus.APPROVED)
                .count();
            long rejectedRequests = requests.stream()
                .filter(req -> req.getApprovalStatus() == ApprovalStatus.REJECTED)
                .count();
            long pendingRequests = requests.stream()
                .filter(req -> req.getApprovalStatus() == ApprovalStatus.PENDING ||
                        req.getApprovalStatus() == ApprovalStatus.OS_APPROVED ||
                        req.getApprovalStatus() == ApprovalStatus.WI_APPROVED ||
                        req.getApprovalStatus() == ApprovalStatus.DPO_APPROVED)
                .count();

            // Group by booking category
            Map<String, Long> categoryStats = requests.stream()
                .filter(req -> req.getBookingCategory() != null)
                .collect(Collectors.groupingBy(Request::getBookingCategory, Collectors.counting()));

            // Group by institute
            Map<String, Long> instituteStats = requests.stream()
                .filter(req -> req.getInstitute() != null)
                .collect(Collectors.groupingBy(Request::getInstitute, Collectors.counting()));

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalRequests", totalRequests);
            stats.put("approvedRequests", approvedRequests);
            stats.put("rejectedRequests", rejectedRequests);
            stats.put("pendingRequests", pendingRequests);
            stats.put("categoryStats", categoryStats);
            stats.put("instituteStats", instituteStats);

            report.put("month", month);
            report.put("reportData", reportData);
            report.put("stats", stats);
            report.put("success", true);

        } catch (DateTimeParseException e) {
            report.put("success", false);
            report.put("error", "Invalid month format. Expected format: yyyy-MM");
        } catch (Exception e) {
            report.put("success", false);
            report.put("error", "Error generating monthly report: " + e.getMessage());
        }

        return report;
    }

    public Map<String, Object> getDateRangeReport(String from, String to) {
        Map<String, Object> report = new HashMap<>();

        try {
            // Parse date parameters
            LocalDate fromDate = LocalDate.parse(from, DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate toDate = LocalDate.parse(to, DateTimeFormatter.ISO_LOCAL_DATE);

            if (fromDate.isAfter(toDate)) {
                report.put("success", false);
                report.put("error", "'from' date cannot be after 'to' date");
                return report;
            }

            List<Request> requests = requestRepository.findAll().stream()
                .filter(req -> req.getRequestDate() != null &&
                        !req.getRequestDate().isBefore(fromDate) &&
                        !req.getRequestDate().isAfter(toDate))
                .toList();

            // Convert requests to reportData format
            List<Map<String, Object>> reportData = requests.stream()
                .map(this::convertRequestToReportData)
                .collect(Collectors.toList());

            // Calculate statistics
            long totalRequests = requests.size();
            long approvedRequests = requests.stream()
                .filter(req -> req.getApprovalStatus() == ApprovalStatus.APPROVED)
                .count();
            long rejectedRequests = requests.stream()
                .filter(req -> req.getApprovalStatus() == ApprovalStatus.REJECTED)
                .count();
            long pendingRequests = requests.stream()
                .filter(req -> req.getApprovalStatus() == ApprovalStatus.PENDING ||
                        req.getApprovalStatus() == ApprovalStatus.OS_APPROVED ||
                        req.getApprovalStatus() == ApprovalStatus.WI_APPROVED ||
                        req.getApprovalStatus() == ApprovalStatus.DPO_APPROVED)
                .count();

            // Group by booking category
            Map<String, Long> categoryStats = requests.stream()
                .filter(req -> req.getBookingCategory() != null)
                .collect(Collectors.groupingBy(Request::getBookingCategory, Collectors.counting()));

            // Group by institute
            Map<String, Long> instituteStats = requests.stream()
                .filter(req -> req.getInstitute() != null)
                .collect(Collectors.groupingBy(Request::getInstitute, Collectors.counting()));

            // Daily breakdown
            Map<String, Long> dailyStats = requests.stream()
                .filter(req -> req.getRequestDate() != null)
                .collect(Collectors.groupingBy(
                    req -> req.getRequestDate().toString(),
                    Collectors.counting()
                ));

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalRequests", totalRequests);
            stats.put("approvedRequests", approvedRequests);
            stats.put("rejectedRequests", rejectedRequests);
            stats.put("pendingRequests", pendingRequests);
            stats.put("categoryStats", categoryStats);
            stats.put("instituteStats", instituteStats);
            stats.put("dailyStats", dailyStats);

            report.put("fromDate", from);
            report.put("toDate", to);
            report.put("reportData", reportData);
            report.put("stats", stats);
            report.put("success", true);

        } catch (DateTimeParseException e) {
            report.put("success", false);
            report.put("error", "Invalid date format. Expected format: yyyy-MM-dd");
        } catch (Exception e) {
            report.put("success", false);
            report.put("error", "Error generating date range report: " + e.getMessage());
        }

        return report;
    }

    public Map<String, Object> getPaymentStatusReport() {
        Map<String, Object> report = new HashMap<>();

        try {
            List<Request> allRequests = requestRepository.findAll();

            // Convert all requests to reportData format
            List<Map<String, Object>> reportData = allRequests.stream()
                .map(this::convertRequestToReportData)
                .collect(Collectors.toList());

            // Count by approval status (treating approval as payment status proxy)
            Map<String, Long> statusStats = allRequests.stream()
                .collect(Collectors.groupingBy(
                    req -> req.getApprovalStatus() != null ? req.getApprovalStatus().name() : "UNKNOWN",
                    Collectors.counting()
                ));

            // Additional payment-related stats (assuming approved = paid, rejected = not paid, pending = pending payment)
            long totalRequests = allRequests.size();
            long paidRequests = allRequests.stream()
                .filter(req -> req.getApprovalStatus() == ApprovalStatus.APPROVED)
                .count();
            long unpaidRequests = allRequests.stream()
                .filter(req -> req.getApprovalStatus() == ApprovalStatus.REJECTED ||
                        req.getApprovalStatus() == ApprovalStatus.PENDING ||
                        req.getApprovalStatus() == ApprovalStatus.OS_APPROVED ||
                        req.getApprovalStatus() == ApprovalStatus.WI_APPROVED ||
                        req.getApprovalStatus() == ApprovalStatus.DPO_APPROVED)
                .count();

            // Calculate payment rate
            double paymentRate = totalRequests > 0 ? (double) paidRequests / totalRequests * 100 : 0;

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalRequests", totalRequests);
            stats.put("paidRequests", paidRequests);
            stats.put("unpaidRequests", unpaidRequests);
            stats.put("paymentRate", String.format("%.2f%%", paymentRate));
            stats.put("statusBreakdown", statusStats);

            report.put("reportData", reportData);
            report.put("stats", stats);
            report.put("success", true);

        } catch (Exception e) {
            report.put("success", false);
            report.put("error", "Error generating payment status report: " + e.getMessage());
        }

        return report;
    }

    private Map<String, Object> convertRequestToReportData(Request request) {
        Map<String, Object> data = new HashMap<>();
        data.put("requestId", request.getRequestId());
        data.put("requestTitle", request.getRequestTitle());
        data.put("institute", request.getInstitute());
        data.put("purpose", request.getPurpose());
        data.put("bookingCategory", request.getBookingCategory());
        data.put("bookingDate", request.getBookingDate() != null ? request.getBookingDate().toString() : null);
        data.put("guests", request.getGuests());
        data.put("applicantFirstName", request.getApplicantFirstName());
        data.put("applicantLastName", request.getApplicantLastName());
        data.put("applicantEmail", request.getApplicantEmail());
        data.put("applicantPhone", request.getApplicantPhone());
        data.put("approvalStatus", request.getApprovalStatus() != null ? request.getApprovalStatus().name() : null);
        data.put("currentApprovalLevel", request.getCurrentApprovalLevel() != null ? request.getCurrentApprovalLevel().name() : null);
        data.put("requestDate", request.getRequestDate() != null ? request.getRequestDate().toString() : null);
        data.put("createdDate", request.getCreatedDate() != null ? request.getCreatedDate().toString() : null);
        return data;
    }
}
