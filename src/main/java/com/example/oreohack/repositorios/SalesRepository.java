package com.example.oreohack.repositorios;

import com.example.oreohack.entidades.Branch;
import com.example.oreohack.entidades.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface SalesRepository extends JpaRepository<Sale, Long> {

    List<Sale> findByBranch(Branch branch);

    @Query("SELECT s FROM Sale s WHERE s.soldAt BETWEEN :from AND :to")
    List<Sale> findByDateRange(@Param("from") Instant from, @Param("to") Instant to);

    @Query("SELECT s FROM Sale s WHERE s.soldAt BETWEEN :from AND :to AND s.branch = :branch")
    List<Sale> findByDateRangeAndBranch(@Param("from") Instant from,
                                        @Param("to") Instant to,
                                        @Param("branch") Branch branch);
}
