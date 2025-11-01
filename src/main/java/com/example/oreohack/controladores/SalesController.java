package com.example.oreohack.controladores;

import com.example.oreohack.dto.request.SaleRequestDTO;
import com.example.oreohack.dto.response.SaleResponseDTO;
import com.example.oreohack.excepciones.NoContentException;
import com.example.oreohack.servicios.SalesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
public class SalesController {

    private final SalesService salesService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // 201
    public SaleResponseDTO createSale(@Valid @RequestBody SaleRequestDTO dto, Authentication auth) {
        return salesService.createSale(dto, auth.getName());
    }

    @GetMapping
    public ResponseEntity<List<SaleResponseDTO>> listSales(Authentication auth,
                                                           @RequestParam(required = false) String branch) {
        List<SaleResponseDTO> sales = salesService.listSales(auth.getName(), branch);
        if (sales.isEmpty()) throw new NoContentException("No hay ventas registradas en el rango dado");
        return ResponseEntity.ok(sales); // 200 OK
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    public void deleteSale(@PathVariable String id) {
        // Este método podría implementarse luego en SalesService
        // y lanzaría UnauthorizedActionException si no es ROLE_CENTRAL
    }
}
