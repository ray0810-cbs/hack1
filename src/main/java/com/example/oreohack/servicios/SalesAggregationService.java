package com.example.oreohack.servicios;

import com.example.oreohack.entidades.Branch;
import com.example.oreohack.entidades.Sale;
import com.example.oreohack.repositorios.SalesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalesAggregationService {

    private final SalesRepository salesRepository;

    public SalesAggregates calculateAggregates(Instant from, Instant to, Branch branch) {
        List<Sale> sales = (branch == null)
                ? salesRepository.findByDateRange(from, to)
                : salesRepository.findByDateRangeAndBranch(from, to, branch);

        if (sales.isEmpty()) return new SalesAggregates(0, 0.0, "N/A", "N/A");

        int totalUnits = sales.stream().mapToInt(Sale::getUnits).sum();
        double totalRevenue = sales.stream().mapToDouble(s -> s.getUnits() * s.getPrice()).sum();

        String topSku = sales.stream()
                .collect(java.util.stream.Collectors.groupingBy(Sale::getSku, java.util.stream.Collectors.summingInt(Sale::getUnits)))
                .entrySet().stream()
                .max(java.util.Map.Entry.comparingByValue())
                .map(java.util.Map.Entry::getKey).orElse("N/A");

        String topBranch = sales.stream()
                .collect(java.util.stream.Collectors.groupingBy(s -> s.getBranch().getName(), java.util.stream.Collectors.summingInt(Sale::getUnits)))
                .entrySet().stream()
                .max(java.util.Map.Entry.comparingByValue())
                .map(java.util.Map.Entry::getKey).orElse("N/A");

        return new SalesAggregates(totalUnits, totalRevenue, topSku, topBranch);
    }

    // Clase interna auxiliar (puedes hacerla record o moverla a dto.response)
    public record SalesAggregates(int totalUnits, double totalRevenue, String topSku, String topBranch) {}
}

