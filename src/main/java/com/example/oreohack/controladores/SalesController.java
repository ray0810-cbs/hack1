package com.example.oreohack.controladores;

import com.example.oreohack.dto.request.SaleRequestDTO;
import com.example.oreohack.dto.response.SaleResponseDTO;
import com.example.oreohack.entidades.UserClass;
import com.example.oreohack.servicios.SalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
public class SalesController {

    private final SalesService salesService;

    @PostMapping
    public ResponseEntity<SaleResponseDTO> create(@RequestBody SaleRequestDTO dto,
                                                  @AuthenticationPrincipal UserClass user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(salesService.createSale(dto, user));
    }

    @GetMapping
    public ResponseEntity<List<SaleResponseDTO>> getAll(@AuthenticationPrincipal UserClass user) {
        return ResponseEntity.ok(salesService.getSales(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleResponseDTO> getById(@PathVariable String id,
                                                   @AuthenticationPrincipal UserClass user) {
        return ResponseEntity.ok(salesService.getSaleById(id, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SaleResponseDTO> update(@PathVariable String id,
                                                  @RequestBody SaleRequestDTO dto,
                                                  @AuthenticationPrincipal UserClass user) {
        return ResponseEntity.ok(salesService.updateSale(id, dto, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id,
                                       @AuthenticationPrincipal UserClass user) {
        salesService.deleteSale(id, user);
        return ResponseEntity.noContent().build();
    }
}

