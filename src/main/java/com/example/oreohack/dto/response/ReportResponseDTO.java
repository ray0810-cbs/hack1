package com.example.oreohack.dto.response;

import lombok.*;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportResponseDTO {
    private String requestId;
    private String status;
    private String message;
    private String estimatedTime;
    private Instant requestedAt;
}
