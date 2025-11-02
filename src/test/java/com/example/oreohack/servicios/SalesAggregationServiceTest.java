package com.example.oreohack.servicios;

import com.example.oreohack.entidades.Branch;
import com.example.oreohack.entidades.Sale;
import com.example.oreohack.repositorios.SalesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalesAggregationServiceTest {

    @Mock
    private SalesRepository salesRepository;

    @InjectMocks
    private SalesAggregationService salesAggregationService;

    private Branch miraflores, sanIsidro;

    @BeforeEach
    void setup() {
        miraflores = Branch.builder().id("1").name("Miraflores").build();
        sanIsidro = Branch.builder().id("2").name("San Isidro").build();
    }

    private Sale createSale(String sku, int units, double price, Branch branch) {
        return Sale.builder()
                .sku(sku)
                .units(units)
                .price(price)
                .branch(branch)
                .soldAt(java.time.LocalDateTime.now())
                .build();
    }

    // ✅ 1) Datos válidos
    @Test
    void shouldCalculateCorrectAggregatesWithValidData() {
        List<Sale> mockSales = List.of(
                createSale("OREO_CLASSIC", 10, 1.99, miraflores),
                createSale("OREO_DOUBLE", 5, 2.49, sanIsidro),
                createSale("OREO_CLASSIC", 15, 1.99, miraflores)
        );

        when(salesRepository.findByDateRange(any(), any())).thenReturn(mockSales);

        var result = salesAggregationService.calculateAggregates(Instant.now().minusSeconds(10000), Instant.now(), null);

        assertThat(result.totalUnits()).isEqualTo(30);
        assertThat(result.totalRevenue()).isEqualTo(10*1.99 + 5*2.49 + 15*1.99);
        assertThat(result.topSku()).isEqualTo("OREO_CLASSIC");
        assertThat(result.topBranch()).isEqualTo("Miraflores");
    }

    // ✅ 2) Lista vacía
    @Test
    void shouldReturnZerosWhenNoSales() {
        when(salesRepository.findByDateRange(any(), any())).thenReturn(List.of());

        var result = salesAggregationService.calculateAggregates(Instant.now(), Instant.now(), null);

        assertThat(result.totalUnits()).isEqualTo(0);
        assertThat(result.totalRevenue()).isEqualTo(0.0);
        assertThat(result.topSku()).isEqualTo("N/A");
        assertThat(result.topBranch()).isEqualTo("N/A");
    }

    // ✅ 3) Filtrado por sucursal
    @Test
    void shouldFilterByBranch() {
        List<Sale> mirafloresSales = List.of(
                createSale("OREO_CLASSIC", 10, 2.0, miraflores),
                createSale("OREO_THINS", 5, 3.0, miraflores)
        );

        when(salesRepository.findByDateRangeAndBranch(any(), any(), eq(miraflores)))
                .thenReturn(mirafloresSales);

        var result = salesAggregationService.calculateAggregates(Instant.now(), Instant.now(), miraflores);

        assertThat(result.totalUnits()).isEqualTo(15);
        assertThat(result.topBranch()).isEqualTo("Miraflores");
    }

    // ✅ 4) Filtrado por fechas (solo se verifica que se llame correctamente)
    @Test
    void shouldQueryWithGivenDates() {
        Instant from = Instant.now().minusSeconds(5000);
        Instant to = Instant.now();
        when(salesRepository.findByDateRange(from, to)).thenReturn(List.of());
        salesAggregationService.calculateAggregates(from, to, null);
        verify(salesRepository, times(1)).findByDateRange(from, to);
    }

    // ✅ 5) Cálculo SKU top con empate
    @Test
    void shouldHandleTiedTopSku() {
        List<Sale> mockSales = List.of(
                createSale("OREO_CLASSIC", 10, 1.0, miraflores),
                createSale("OREO_DOUBLE", 10, 1.0, miraflores)
        );
        when(salesRepository.findByDateRange(any(), any())).thenReturn(mockSales);

        var result = salesAggregationService.calculateAggregates(Instant.now(), Instant.now(), null);

        // cualquiera de los dos es válido, pero no debe lanzar error
        assertThat(List.of("OREO_CLASSIC", "OREO_DOUBLE")).contains(result.topSku());
    }
}
