package com.mri.mri_backend.Controller;

import com.mri.mri_backend.Dto.BlockedDateCreationDTO;
import com.mri.mri_backend.Dto.BlockedDateDTO;
import com.mri.mri_backend.model.BlockedDate;
import com.mri.mri_backend.service.BlockedDateService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/blocked-dates")
@CrossOrigin(origins = "http://maldarailwayinstitute.in")
public class BlockedDateController {

    private final BlockedDateService blockedDateService;

    public BlockedDateController(BlockedDateService blockedDateService) {
        this.blockedDateService = blockedDateService;
    }

    @GetMapping
    public ResponseEntity<List<BlockedDateDTO>> getAllBlockedDates() {
        List<BlockedDate> blockedDates = blockedDateService.getAllBlockedDates();
        List<BlockedDateDTO> dtos = blockedDates.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/institute/{institute}")
    public ResponseEntity<List<BlockedDateDTO>> getBlockedDatesByInstitute(@PathVariable String institute) {
        List<BlockedDate> blockedDates = blockedDateService.getBlockedDatesByInstitute(institute);
        List<BlockedDateDTO> dtos = blockedDates.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> isDateBlocked(
            @RequestParam String institute,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        boolean isBlocked = blockedDateService.isDateBlocked(institute, date);
        return ResponseEntity.ok(isBlocked);
    }

    @GetMapping("/range")
    public ResponseEntity<List<BlockedDateDTO>> getBlockedDatesInRange(
            @RequestParam String institute,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<BlockedDate> blockedDates = blockedDateService.getBlockedDatesInRange(institute, startDate, endDate);
        List<BlockedDateDTO> dtos = blockedDates.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<BlockedDateDTO> blockDate(@RequestBody BlockedDateCreationDTO creationDTO) {
        try {
            BlockedDate blockedDate = blockedDateService.blockDate(
                    creationDTO.getInstitute(),
                    creationDTO.getBlockedDate(),
                    creationDTO.getBlockedBy(),
                    creationDTO.getReason()
            );
            return ResponseEntity.ok(convertToDTO(blockedDate));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/unblock")
    public ResponseEntity<Void> unblockDate(
            @RequestParam String institute,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        blockedDateService.unblockDate(institute, date);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlockedDate(@PathVariable Long id) {
        blockedDateService.deleteBlockedDate(id);
        return ResponseEntity.ok().build();
    }

    private BlockedDateDTO convertToDTO(BlockedDate blockedDate) {
        return new BlockedDateDTO(
                blockedDate.getId(),
                blockedDate.getInstitute(),
                blockedDate.getBlockedDate(),
                blockedDate.getBlockedBy(),
                blockedDate.getBlockedAt(),
                blockedDate.getReason()
        );
    }
}
