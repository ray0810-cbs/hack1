package com.example.oreohack.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.oreohack.entidades.Sale;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SalesRepository extends JpaRepository<Sale, String> {

    // Todas las ventas de una sucursal específica
    List<Sale> findByBranch(String branch);

    // Ventas en un rango de fechas (para reportes globales)
    List<Sale> findBySoldAtBetween(LocalDateTime from, LocalDateTime to);

    // Ventas de una sucursal dentro de un rango de fechas (para usuarios BRANCH)
    List<Sale> findByBranchAndSoldAtBetween(String branch, LocalDateTime from, LocalDateTime to);
}
