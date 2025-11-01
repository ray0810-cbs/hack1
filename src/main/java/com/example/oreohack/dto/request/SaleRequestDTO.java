package com.example.oreohack.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SaleRequestDTO {
    @NotBlank
    private String sku;

    @Min(1)
    private int units;

    @Min(0)
    private double price;

    @NotBlank
    private String branch;

    @NotNull
    private LocalDateTime soldAt;
}
