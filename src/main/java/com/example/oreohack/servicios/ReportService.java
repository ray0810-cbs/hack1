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
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ApplicationEventPublisher eventPublisher;

    public ReportResponseDTO requestWeeklyReport(ReportRequestDTO dto, UserClass user) {
        // 📨 Validar correo destinatario
        if (dto.getEmailTo() == null || dto.getEmailTo().isBlank()) {
            throw new InvalidRequestException("Debe especificar el correo destinatario del reporte.");
        }

        // 🏢 Validar sucursal solicitada
        String finalBranch;
        if (user.getRole() == Role.CENTRAL) {
            if (dto.getBranch() == null || dto.getBranch().isBlank()) {
                throw new InvalidRequestException("Debe especificar la sucursal para el reporte.");
            }
            finalBranch = dto.getBranch();
        }
        else if (user.getRole() == Role.BRANCH) {
            if (!dto.getBranch().equalsIgnoreCase(user.getBranch().getName())) {
                throw new ForbiddenActionException("Usuario no autorizado para generar reportes de otra sucursal.");
            }
            finalBranch = user.getBranch().getName(); // fuerza consistencia
        }
        else {
            throw new ForbiddenActionException("Rol no autorizado para solicitar reportes.");
        }

        // 🪪 Generar ID único
        String requestId = "req_" + UUID.randomUUID().toString().substring(0, 8);

        // 🚀 Publicar evento con DTO limpio
        ReportRequestDTO cleanDto = ReportRequestDTO.builder()
                .from(dto.getFrom())
                .to(dto.getTo())
                .branch(finalBranch)
                .emailTo(dto.getEmailTo())
                .build();

        eventPublisher.publishEvent(new ReportRequestedEvent(this, cleanDto, user));

        // 📦 Respuesta inmediata
        return ReportResponseDTO.builder()
                .requestId(requestId)
                .status("PROCESSING")
                .message("Su solicitud de reporte está siendo procesada. Recibirá el resumen en %s en unos momentos."
                        .formatted(dto.getEmailTo()))
                .estimatedTime("30–60 segundos")
                .requestedAt(Instant.now())
                .build();
    }


}

