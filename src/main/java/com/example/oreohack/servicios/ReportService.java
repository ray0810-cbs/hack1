package com.example.oreohack.servicios;

import com.example.oreohack.dto.request.ReportRequestDTO;
import com.example.oreohack.dto.response.ReportResponseDTO;
import com.example.oreohack.entidades.*;
import com.example.oreohack.entidades.roles.ReportStatus;
import com.example.oreohack.eventos.ReportRequestedEvent;
import com.example.oreohack.excepciones.*;
import com.example.oreohack.repositorios.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRequestRepository reportRequestRepository;
    private final BranchRepository branchRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public ReportResponseDTO requestReport(ReportRequestDTO dto, String username) {
        LocalDate from = (dto.getFrom() == null)
                ? LocalDate.now().minusDays(7)
                : LocalDate.parse(dto.getFrom(), DateTimeFormatter.ISO_DATE);

        LocalDate to = (dto.getTo() == null)
                ? LocalDate.now()
                : LocalDate.parse(dto.getTo(), DateTimeFormatter.ISO_DATE);

        Branch branch = branchRepository.findByName(dto.getBranch())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada"));

        ReportRequest report = ReportRequest.builder()
                .fromDate(from)
                .toDate(to)
                .branch(branch)
                .emailTo(dto.getEmailTo())
                .status(ReportStatus.PROCESSING)
                .requestedAt(Instant.now())
                .build();

        reportRequestRepository.save(report);

        eventPublisher.publishEvent(new ReportRequestedEvent(this, report));

        return ReportResponseDTO.builder()
                .requestId(report.getId())
                .status(report.getStatus().name())
                .message("Su solicitud de reporte está siendo procesada. Recibirá el resumen en " + dto.getEmailTo() + " en unos momentos.")
                .estimatedTime("30-60 segundos")
                .requestedAt(report.getRequestedAt())
                .build();
    }

    // Método llamado por el listener
    public void processReportAsync(ReportRequest request) {
        try {
            // Aquí iría la lógica real: calcular agregados, llamar LLM, enviar correo
            request.setStatus(ReportStatus.COMPLETED);
            reportRequestRepository.save(request);
        } catch (Exception e) {
            request.setStatus(ReportStatus.FAILED);
            throw new ExternalServiceException("Error al procesar el reporte: " + e.getMessage());
        }
    }
}
