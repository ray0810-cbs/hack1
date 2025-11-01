package com.example.oreohack.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SummaryResponseDTO {
    private long totalUnits;
    private double totalRevenue;
    private String topSku;
    private String topBranch;
    private String summaryText; // respuesta del LLM
}

