package com.example.oreohack.dto.request;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class VentaRequestDTO {
    private String sku;
    private Integer units;
    private Double price;
    private String branch;
    private LocalDateTime soldAt;
}

