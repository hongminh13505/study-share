package com.studydocs.repository;

import com.studydocs.model.entity.Report;
import com.studydocs.model.enums.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {
    
    Page<Report> findByStatus(ReportStatus status, Pageable pageable);
    
    Page<Report> findByDocument_DocumentId(Integer documentId, Pageable pageable);
    
    long countByStatus(ReportStatus status);
}

