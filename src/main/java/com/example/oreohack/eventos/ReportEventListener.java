package com.example.oreohack.eventos;

import com.example.oreohack.entidades.ReportRequest;
import com.example.oreohack.entidades.roles.ReportStatus;
import com.example.oreohack.servicios.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportEventListener {

    private final ReportService reportService;

    @Async
    @EventListener
    public void handleReportRequest(ReportRequestedEvent event) {
        ReportRequest request = event.getReportRequest();
        try {
            reportService.processReportAsync(request);
        } catch (Exception e) {
            request.setStatus(ReportStatus.FAILED);
        }
    }
}
