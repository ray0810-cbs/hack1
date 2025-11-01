package com.example.oreohack.servicios;

import com.example.oreohack.dto.request.SaleRequestDTO;
import com.example.oreohack.dto.response.SaleResponseDTO;
import com.example.oreohack.entidades.*;
import com.example.oreohack.excepciones.*;
import com.example.oreohack.repositorios.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalesService {

    private final SaleRepository saleRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    @Transactional
    public SaleResponseDTO createSale(SaleRequestDTO dto, String username) {
        UserClass user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (user.getRole().name().equals("BRANCH") &&
                !user.getBranch().getName().equalsIgnoreCase(dto.getBranch())) {
            throw new UnauthorizedActionException("No puede registrar ventas de otra sucursal");
        }

        Branch branch = branchRepository.findByName(dto.getBranch())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada"));

        Sale sale = mapper.map(dto, Sale.class);
        sale.setBranch(branch);
        sale.setCreatedBy(user);
        saleRepository.save(sale);
        return mapper.map(sale, SaleResponseDTO.class);
    }

    public List<SaleResponseDTO> listSales(String username, String branchFilter) {
        UserClass user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        List<Sale> sales;
        if (user.getRole().name().equals("CENTRAL")) {
            sales = saleRepository.findAll();
        } else {
            sales = saleRepository.findByBranchAndDateRange(
                    user.getBranch(), null, null);
        }

        if (sales.isEmpty()) throw new NoContentException("No hay ventas registradas");

        return sales.stream()
                .map(s -> mapper.map(s, SaleResponseDTO.class))
                .collect(Collectors.toList());
    }
}

