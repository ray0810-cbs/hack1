package com.example.oreohack.servicios;

import com.example.oreohack.dto.request.SaleRequestDTO;
import com.example.oreohack.dto.response.SaleResponseDTO;
import com.example.oreohack.entidades.*;
import com.example.oreohack.entidades.roles.Role;
import com.example.oreohack.excepciones.*;
import com.example.oreohack.repositorios.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalesService {

    private final SalesRepository salesRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    @Transactional
    public SaleResponseDTO createSale(SaleRequestDTO dto, UserClass user) {
        Branch branch;

        // 🔹 Caso 1: CENTRAL — puede elegir cualquier sucursal
        if (user.getRole() == Role.CENTRAL) {
            branch = branchRepository.findByName(dto.getBranch())
                    .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada: " + dto.getBranch()));
        }
        // 🔹 Caso 2: BRANCH — siempre usa su propia sucursal, ignora el DTO
        else if (user.getRole() == Role.BRANCH) {
            branch = user.getBranch();
        }
        // 🔹 (Opcional) seguridad adicional: cualquier otro rol es inválido
        else {
            throw new ForbiddenActionException("Rol no autorizado para registrar ventas");
        }

        // 🔸 Construcción del objeto venta
        Sale sale = Sale.builder()
                .sku(dto.getSku())
                .units(dto.getUnits())
                .price(dto.getPrice())
                .branch(branch)
                .soldAt(dto.getSoldAt() != null ? dto.getSoldAt() : Instant.now())
                .createdBy(user)
                .createdAt(Instant.now())
                .build();

        salesRepository.save(sale);

        // 🔸 Respuesta DTO
        SaleResponseDTO responseDTO = mapper.map(sale, SaleResponseDTO.class);
        responseDTO.setCreatedBy(user.getUsername());
        responseDTO.setBranch(branch.getName());
        return responseDTO;
    }

    @Transactional(readOnly = true)
    public List<SaleResponseDTO> getSales(UserClass user) {
        List<Sale> sales;

        if (user.getRole() == Role.CENTRAL) {
            sales = salesRepository.findAll();
        } else if (user.getRole() == Role.BRANCH) {
            sales = salesRepository.findByBranch(user.getBranch());
        } else {
            throw new ForbiddenActionException("Rol no autorizado para consultar ventas");
        }

        return sales.stream()
                .map(sale -> {
                    SaleResponseDTO dto = mapper.map(sale, SaleResponseDTO.class);
                    dto.setBranch(sale.getBranch().getName());
                    dto.setCreatedBy(sale.getCreatedBy().getUsername());
                    return dto;
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public SaleResponseDTO getSaleById(String id, UserClass user) {
        Sale sale = salesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada"));

        if (user.getRole() == Role.BRANCH && !sale.getBranch().equals(user.getBranch())) {
            throw new ForbiddenActionException("No puede acceder a ventas de otra sucursal");
        }

        SaleResponseDTO dto = mapper.map(sale, SaleResponseDTO.class);
        dto.setBranch(sale.getBranch().getName());
        dto.setCreatedBy(sale.getCreatedBy().getUsername());
        return dto;
    }

    @Transactional
    public SaleResponseDTO updateSale(String id, SaleRequestDTO dto, UserClass user) {
        Sale sale = salesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada"));

        if (user.getRole() == Role.BRANCH && !sale.getBranch().equals(user.getBranch())) {
            throw new ForbiddenActionException("No puede modificar ventas de otra sucursal");
        }

        sale.setSku(dto.getSku());
        sale.setUnits(dto.getUnits());
        sale.setPrice(dto.getPrice());
        sale.setSoldAt(dto.getSoldAt() != null ? dto.getSoldAt() : Instant.now());

        salesRepository.save(sale);

        SaleResponseDTO dtoResponse = mapper.map(sale, SaleResponseDTO.class);
        dtoResponse.setBranch(sale.getBranch().getName());
        dtoResponse.setCreatedBy(sale.getCreatedBy().getUsername());
        return dtoResponse;
    }

    @Transactional
    public void deleteSale(String id, UserClass user) {
        if (user.getRole() == Role.BRANCH)
            throw new ForbiddenActionException("Solo ROLE_CENTRAL puede eliminar ventas");

        Sale sale = salesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada"));

        salesRepository.delete(sale);
    }

}


