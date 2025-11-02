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
        // 📨 Validar correo destinatario
        if (dto.getEmailTo() == null || dto.getEmailTo().isBlank()) {
            throw new InvalidRequestException("Debe especificar el correo destinatario del reporte.");
        }

        // 🏢 Validar sucursal solicitada
        if (user.getRole() == Role.CENTRAL) {
            // CENTRAL puede solicitar reportes de cualquier sucursal existente
            if (dto.getBranch() == null || dto.getBranch().isBlank()) {
                throw new InvalidRequestException("Debe especificar la sucursal para el reporte.");
            }
        }
        else if (user.getRole() == Role.BRANCH) {
            // BRANCH solo puede solicitar reportes de su propia sucursal
            dto.setBranch(user.getBranch().getName());
        }
        else {
            throw new ForbiddenActionException("Rol no autorizado para solicitar reportes.");
        }

        // 🪪 Generar ID único para el reporte
        String requestId = "req_" + UUID.randomUUID().toString().substring(0, 8);

        // 🚀 Publicar el evento asincrónico
        eventPublisher.publishEvent(new ReportRequestedEvent(this, dto, user));

        // 📦 Respuesta inmediata al cliente
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

