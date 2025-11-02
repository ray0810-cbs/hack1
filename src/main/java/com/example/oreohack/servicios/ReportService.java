package com.example.oreohack.servicios;

import com.example.oreohack.dto.request.ReportRequestDTO;
import com.example.oreohack.dto.response.ReportResponseDTO;
import com.example.oreohack.entidades.UserClass;
import com.example.oreohack.entidades.roles.Role;
import com.example.oreohack.eventos.ReportRequestedEvent;
import com.example.oreohack.excepciones.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ApplicationEventPublisher eventPublisher;

    public ReportResponseDTO requestWeeklyReport(ReportRequestDTO dto, UserClass user) {
        if (dto.getEmailTo() == null || dto.getEmailTo().isEmpty())
            throw new InvalidRequestException("Debe especificar el correo destinatario");

        if (user.getRole() == Role.BRANCH && !user.getBranch().getName().equals(dto.getBranch()))
            throw new ForbiddenActionException("No puede generar reportes de otra sucursal");

        String requestId = "req_" + UUID.randomUUID().toString().substring(0, 8);

        eventPublisher.publishEvent(new ReportRequestedEvent(this, dto, user));

        return ReportResponseDTO.builder()
                .requestId(requestId)
                .status("PROCESSING")
                .message("Su solicitud de reporte está siendo procesada. Recibirá el resumen en " + dto.getEmailTo() + " en unos momentos.")
                .estimatedTime("30-60 segundos")
                .requestedAt(Instant.now())
                .build();
    }
}

