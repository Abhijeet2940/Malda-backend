package com.mri.mri_backend.service;

import com.mri.mri_backend.model.BlockedDate;
import com.mri.mri_backend.repository.BlockedDateRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BlockedDateService {

    private final BlockedDateRepository blockedDateRepository;

    public BlockedDateService(BlockedDateRepository blockedDateRepository) {
        this.blockedDateRepository = blockedDateRepository;
    }

    public List<BlockedDate> getAllBlockedDates() {
        return blockedDateRepository.findAll();
    }

    public List<BlockedDate> getBlockedDatesByInstitute(String institute) {
        return blockedDateRepository.findByInstitute(institute);
    }

    public List<BlockedDate> getBlockedDatesInRange(String institute, LocalDate startDate, LocalDate endDate) {
        return blockedDateRepository.findBlockedDatesInRange(institute, startDate, endDate);
    }

    public boolean isDateBlocked(String institute, LocalDate date) {
        return blockedDateRepository.existsByInstituteAndBlockedDate(institute, date);
    }

    public BlockedDate blockDate(String institute, LocalDate blockedDate, String blockedBy, String reason) {
        if (isDateBlocked(institute, blockedDate)) {
            throw new IllegalArgumentException("Date is already blocked for this institute");
        }

        BlockedDate blockedDateEntity = new BlockedDate(institute, blockedDate, blockedBy, reason);
        return blockedDateRepository.save(blockedDateEntity);
    }

    public void unblockDate(String institute, LocalDate blockedDate) {
        List<BlockedDate> blockedDates = blockedDateRepository.findByInstituteAndBlockedDate(institute, blockedDate);
        if (!blockedDates.isEmpty()) {
            blockedDateRepository.deleteAll(blockedDates);
        }
    }

    public Optional<BlockedDate> getBlockedDateById(Long id) {
        return blockedDateRepository.findById(id);
    }

    public void deleteBlockedDate(Long id) {
        blockedDateRepository.deleteById(id);
    }
}