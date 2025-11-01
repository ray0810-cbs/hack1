package com.example.oreohack.controladores;

import com.example.oreohack.dto.request.ReportRequestDTO;
import com.example.oreohack.dto.response.ReportResponseDTO;
import com.example.oreohack.servicios.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/sales/summary")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/weekly")
    @ResponseStatus(HttpStatus.ACCEPTED) // 202
    public ReportResponseDTO requestWeeklyReport(@Valid @RequestBody ReportRequestDTO dto,
                                                 Authentication auth) {
        return reportService.requestReport(dto, auth.getName());
    }
}

