package com.example.oreohack.repositorios;

import com.example.oreohack.entidades.Branch;
import com.example.oreohack.entidades.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, String> {

    @Query("SELECT s FROM Sale s WHERE s.soldAt BETWEEN :from AND :to")
    List<Sale> findByDateRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT s FROM Sale s WHERE s.branch = :branch AND s.soldAt BETWEEN :from AND :to")
    List<Sale> findByBranchAndDateRange(@Param("branch") Branch branch,
                                        @Param("from") LocalDateTime from,
                                        @Param("to") LocalDateTime to);
}
