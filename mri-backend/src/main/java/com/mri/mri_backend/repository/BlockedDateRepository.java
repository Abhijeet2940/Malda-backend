package com.mri.mri_backend.repository;

import com.mri.mri_backend.model.BlockedDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BlockedDateRepository extends JpaRepository<BlockedDate, Long> {

    List<BlockedDate> findByInstitute(String institute);

    List<BlockedDate> findByInstituteAndBlockedDate(String institute, LocalDate blockedDate);

    @Query("SELECT bd FROM BlockedDate bd WHERE bd.institute = :institute AND bd.blockedDate >= :startDate AND bd.blockedDate <= :endDate")
    List<BlockedDate> findBlockedDatesInRange(@Param("institute") String institute,
                                             @Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);

    boolean existsByInstituteAndBlockedDate(String institute, LocalDate blockedDate);
}