package com.example.oreohack.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponseDTO {
    private String token;
    private int expiresIn;
    private String role;
    private String branch;
}