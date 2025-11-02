package com.example.oreohack.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant soldAt;
    private String createdBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant createdAt;
}
