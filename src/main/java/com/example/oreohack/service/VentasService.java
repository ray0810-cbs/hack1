package com.example.oreohack.service;

import com.example.oreohack.dto.request.VentaRequestDTO;
import com.example.oreohack.dto.response.VentaResponseDTO;
import com.example.oreohack.entidades.Sale;
import com.example.oreohack.entidades.UserClass;
import com.example.oreohack.repositorios.SalesRepository;
import com.example.oreohack.repositorios.UserClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VentasService {

    private final SalesRepository salesRepository;
    private final UserClassRepository userRepository;

    // ==============================
    // Crear una nueva venta
    // ==============================
    @Transactional
    public VentaResponseDTO crearVenta(VentaRequestDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        UserClass user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));

        // Reglas de negocio:
        // Si es BRANCH, solo puede crear ventas en su propia sucursal
        if (user.getRole() == UserClass.Role.BRANCH && !user.getBranch().equalsIgnoreCase(dto.getBranch())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes crear ventas para otra sucursal.");
        }

        // Crear venta
        Sale sale = Sale.builder()
                .sku(dto.getSku())
                .units(dto.getUnits())
                .price(dto.getPrice())
                .branch(dto.getBranch())
                .soldAt(dto.getSoldAt() != null ? dto.getSoldAt() : LocalDateTime.now())
                .createdBy(user)
                .build();

        salesRepository.save(sale);

        return mapToResponse(sale);
    }

    // ==============================
    // Obtener detalle de una venta
    // ==============================
    @Transactional(readOnly = true)
    public VentaResponseDTO obtenerVenta(String id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        UserClass user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));

        Sale sale = salesRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venta no encontrada"));

        // BRANCH solo puede ver sus propias ventas
        if (user.getRole() == UserClass.Role.BRANCH && !sale.getBranch().equalsIgnoreCase(user.getBranch())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes acceder a ventas de otra sucursal.");
        }

        return mapToResponse(sale);
    }

    // ==============================
    // Listar ventas con filtros
    // ==============================
    @Transactional(readOnly = true)
    public List<VentaResponseDTO> listarVentas(LocalDateTime from, LocalDateTime to, String branch) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        UserClass user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));

        List<Sale> ventas;

        if (user.getRole() == UserClass.Role.BRANCH) {
            ventas = salesRepository.findByBranchAndSoldAtBetween(user.getBranch(), from, to);
        } else {
            if (branch != null && !branch.isBlank()) {
                ventas = salesRepository.findByBranchAndSoldAtBetween(branch, from, to);
            } else {
                ventas = salesRepository.findBySoldAtBetween(from, to);
            }
        }

        return ventas.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ==============================
    // Actualizar venta
    // ==============================
    @Transactional
    public VentaResponseDTO actualizarVenta(String id, VentaRequestDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        UserClass user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));

        Sale sale = salesRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venta no encontrada"));

        if (user.getRole() == UserClass.Role.BRANCH &&
                !sale.getBranch().equalsIgnoreCase(user.getBranch())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes modificar ventas de otra sucursal.");
        }

        sale.setSku(dto.getSku());
        sale.setUnits(dto.getUnits());
        sale.setPrice(dto.getPrice());
        sale.setBranch(dto.getBranch());
        sale.setSoldAt(dto.getSoldAt());

        salesRepository.save(sale);
        return mapToResponse(sale);
    }

    // ==============================
    // Eliminar venta
    // ==============================
    @Transactional
    public void eliminarVenta(String id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        UserClass user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));

        if (user.getRole() != UserClass.Role.CENTRAL) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo usuarios CENTRAL pueden eliminar ventas.");
        }

        salesRepository.deleteById(id);
    }

    // ==============================
    // Mapper auxiliar
    // ==============================
    private VentaResponseDTO mapToResponse(Sale sale) {
        return VentaResponseDTO.builder()
                .id(sale.getId())
                .sku(sale.getSku())
                .units(sale.getUnits())
                .price(sale.getPrice())
                .branch(sale.getBranch())
                .soldAt(sale.getSoldAt())
                .createdBy(sale.getCreatedBy().getUsername())
                .build();
    }
}


