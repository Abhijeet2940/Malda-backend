package com.mri.mri_backend.repository;

import com.mri.mri_backend.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;

public interface  RequestRepository extends JpaRepository<Request, Long> {
}
