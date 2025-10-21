package com.example.oreohack.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.oreohack.entidades.ReportRequest;

@Repository
public interface ReportRequestRepository extends JpaRepository<ReportRequest, String> {
}
