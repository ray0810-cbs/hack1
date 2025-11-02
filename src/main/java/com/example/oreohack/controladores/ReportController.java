package com.example.oreohack.controladores;

import com.example.oreohack.dto.request.ReportRequestDTO;
import com.example.oreohack.dto.response.ReportResponseDTO;
import com.example.oreohack.entidades.UserClass;
import com.example.oreohack.servicios.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sales/summary")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/weekly")
    public ResponseEntity<ReportResponseDTO> generateWeekly(@RequestBody ReportRequestDTO dto,
                                                            @AuthenticationPrincipal UserClass user) {
        ReportResponseDTO response = reportService.requestWeeklyReport(dto, user);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}

