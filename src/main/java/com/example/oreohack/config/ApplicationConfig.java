package com.example.oreohack.config;

import com.example.oreohack.dto.response.SaleResponseDTO;
import com.example.oreohack.entidades.Sale;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true);

        mapper.typeMap(Sale.class, SaleResponseDTO.class).addMappings(m -> {
            m.map(src -> src.getBranch().getName(), SaleResponseDTO::setBranch);
            m.map(src -> src.getCreatedBy().getUsername(), SaleResponseDTO::setCreatedBy);
            m.map(Sale::getSoldAt, SaleResponseDTO::setSoldAt);
            m.map(Sale::getCreatedAt, SaleResponseDTO::setCreatedAt);
        });

        return mapper;
    }
}
