package com.mri.mri_backend.Controller;

import com.mri.mri_backend.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "http://maldarailwayinstitute.in")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/monthly")
    public ResponseEntity<Map<String, Object>> monthly(@RequestParam String month) {
        Map<String, Object> report = reportService.getMonthlyReport(month);
        if ((Boolean) report.get("success")) {
            return ResponseEntity.ok(report);
        } else {
            return ResponseEntity.badRequest().body(report);
        }
    }

    @GetMapping("/date-range")
    public ResponseEntity<Map<String, Object>> dateRange(@RequestParam String from, @RequestParam String to) {
        Map<String, Object> report = reportService.getDateRangeReport(from, to);
        if ((Boolean) report.get("success")) {
            return ResponseEntity.ok(report);
        } else {
            return ResponseEntity.badRequest().body(report);
        }
    }

    @GetMapping("/payment-status")
    public ResponseEntity<Map<String, Object>> paymentStatus() {
        Map<String, Object> report = reportService.getPaymentStatusReport();
        if ((Boolean) report.get("success")) {
            return ResponseEntity.ok(report);
        } else {
            return ResponseEntity.badRequest().body(report);
        }
    }
}
