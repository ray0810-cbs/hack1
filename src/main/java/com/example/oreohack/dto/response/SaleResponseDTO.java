package com.example.oreohack.dto.response;

import lombok.*;
import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleResponseDTO {
    private String id;
    private String sku;
    private int units;
    private double price;
    private String branch;
    private LocalDateTime soldAt;
    private String createdBy;
    private Instant createdAt;
}
