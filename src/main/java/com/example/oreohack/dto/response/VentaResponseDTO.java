package com.example.oreohack.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class VentaResponseDTO {
    private String id;
    private String sku;
    private Integer units;
    private Double price;
    private String branch;
    private LocalDateTime soldAt;
    private String createdBy;
}

