package com.example.oreohack.repositorios;

import com.example.oreohack.entidades.ReportRequest;
import com.example.oreohack.entidades.roles.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRequestRepository extends JpaRepository<ReportRequest, String> {
    List<ReportRequest> findByStatus(ReportStatus status);
}
