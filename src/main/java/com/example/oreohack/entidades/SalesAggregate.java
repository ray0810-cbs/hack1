package com.example.oreohack.entidades;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesAggregate {

    private long totalUnits;
    private double totalRevenue;
    private String topSku;
    private String topBranch;
}
