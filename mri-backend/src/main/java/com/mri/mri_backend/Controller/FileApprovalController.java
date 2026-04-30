package com.mri.mri_backend.Controller;

import com.mri.mri_backend.model.Request;
import com.mri.mri_backend.repository.RequestRepository;
import com.mri.mri_backend.util.FileConverterUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "http://localhost:5173")
public class FileApprovalController {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private FileConverterUtil fileConverterUtil;

    // Get files for approval
    @GetMapping("/approval/{requestId}")
    public ResponseEntity<Map<String, Object>> getFilesForApproval(@PathVariable Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow();

        try {
            Map<String, Object> filesJson = new ObjectMapper()
                .readValue(request.getFileApprovalData(), Map.class);
            return ResponseEntity.ok(filesJson);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Approve file
    @PutMapping("/approve/{requestId}/{fileType}")
    public ResponseEntity<String> approveFile(
            @PathVariable Long requestId,
            @PathVariable String fileType,
            @RequestParam String approvedBy) {

        Request request = requestRepository.findById(requestId).orElseThrow();

        try {
            Map<String, Object> filesJson = new ObjectMapper()
                .readValue(request.getFileApprovalData(), Map.class);

            if (filesJson.containsKey(fileType)) {
                @SuppressWarnings("unchecked")
                Map<String, Object> fileJson = (Map<String, Object>) filesJson.get(fileType);
                fileConverterUtil.approveFile(fileJson, approvedBy);

                // Save updated JSON
                request.setFileApprovalData(new ObjectMapper().writeValueAsString(filesJson));
                requestRepository.save(request);

                return ResponseEntity.ok("File approved successfully");
            }
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error approving file: " + e.getMessage());
        }
    }

    // Reject file
    @PutMapping("/reject/{requestId}/{fileType}")
    public ResponseEntity<String> rejectFile(
            @PathVariable Long requestId,
            @PathVariable String fileType,
            @RequestParam String rejectedBy,
            @RequestParam String reason) {

        Request request = requestRepository.findById(requestId).orElseThrow();

        try {
            Map<String, Object> filesJson = new ObjectMapper()
                .readValue(request.getFileApprovalData(), Map.class);

            if (filesJson.containsKey(fileType)) {
                @SuppressWarnings("unchecked")
                Map<String, Object> fileJson = (Map<String, Object>) filesJson.get(fileType);
                fileConverterUtil.rejectFile(fileJson, rejectedBy, reason);

                // Save updated JSON
                request.setFileApprovalData(new ObjectMapper().writeValueAsString(filesJson));
                requestRepository.save(request);

                return ResponseEntity.ok("File rejected successfully");
            }
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error rejecting file: " + e.getMessage());
        }
    }
}
