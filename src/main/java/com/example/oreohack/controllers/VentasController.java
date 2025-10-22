package com.example.oreohack.controllers;

import com.example.oreohack.dto.request.VentaRequestDTO;
import com.example.oreohack.dto.response.VentaResponseDTO;
import com.example.oreohack.service.VentasService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
public class VentasController {

    private final VentasService ventasService;

    // Crear nueva venta
    @PostMapping
    public ResponseEntity<VentaResponseDTO> crearVenta(@RequestBody VentaRequestDTO dto) {
        VentaResponseDTO response = ventasService.crearVenta(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Obtener detalle de venta
    @GetMapping("/{id}")
    public ResponseEntity<VentaResponseDTO> obtenerVenta(@PathVariable String id) {
        VentaResponseDTO response = ventasService.obtenerVenta(id);
        return ResponseEntity.ok(response);
    }

    // Listar ventas con filtros (por fecha y sucursal)
    @GetMapping
    public ResponseEntity<List<VentaResponseDTO>> listarVentas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) String branch) {
        List<VentaResponseDTO> response = ventasService.listarVentas(from, to, branch);
        return ResponseEntity.ok(response);
    }

    // Actualizar venta
    @PutMapping("/{id}")
    public ResponseEntity<VentaResponseDTO> actualizarVenta(@PathVariable String id,
                                                            @RequestBody VentaRequestDTO dto) {
        VentaResponseDTO response = ventasService.actualizarVenta(id, dto);
        return ResponseEntity.ok(response);
    }

    // Eliminar venta
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVenta(@PathVariable String id) {
        ventasService.eliminarVenta(id);
        return ResponseEntity.noContent().build();
    }
}
