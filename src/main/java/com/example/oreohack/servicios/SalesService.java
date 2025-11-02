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
        Branch branch = branchRepository.findByName(dto.getBranch())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada"));

        if (user.getRole() == Role.BRANCH && !branch.equals(user.getBranch()))
            throw new ForbiddenActionException("No puede registrar ventas de otra sucursal");

        Sale sale = Sale.builder()
                .sku(dto.getSku())
                .units(dto.getUnits())
                .price(dto.getPrice())
                .branch(branch)
                .soldAt(dto.getSoldAt())
                .createdBy(user)
                .createdAt(Instant.now())
                .build();

        salesRepository.save(sale);
        return mapper.map(sale, SaleResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public List<SaleResponseDTO> getSales(UserClass user) {
        List<Sale> sales = (user.getRole() == Role.CENTRAL)
                ? salesRepository.findAll()
                : salesRepository.findByBranch(user.getBranch());
        return sales.stream().map(s -> mapper.map(s, SaleResponseDTO.class)).toList();
    }

    @Transactional(readOnly = true)
    public SaleResponseDTO getSaleById(Long id, UserClass user) {
        Sale sale = salesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada"));
        if (user.getRole() == Role.BRANCH && !sale.getBranch().equals(user.getBranch()))
            throw new ForbiddenActionException("No puede acceder a ventas de otra sucursal");
        return mapper.map(sale, SaleResponseDTO.class);
    }

    @Transactional
    public SaleResponseDTO updateSale(Long id, SaleRequestDTO dto, UserClass user) {
        Sale sale = salesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada"));

        if (user.getRole() == Role.BRANCH && !sale.getBranch().equals(user.getBranch()))
            throw new ForbiddenActionException("No puede modificar ventas de otra sucursal");

        sale.setSku(dto.getSku());
        sale.setUnits(dto.getUnits());
        sale.setPrice(dto.getPrice());
        sale.setSoldAt(dto.getSoldAt());
        salesRepository.save(sale);

        return mapper.map(sale, SaleResponseDTO.class);
    }

    @Transactional
    public void deleteSale(Long id, UserClass user) {
        if (user.getRole() == Role.BRANCH)
            throw new ForbiddenActionException("Solo ROLE_CENTRAL puede eliminar ventas");

        Sale sale = salesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada"));
        salesRepository.delete(sale);
    }
}


