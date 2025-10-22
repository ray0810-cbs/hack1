package com.example.oreohack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDTO {
    private String token;
    private Long expiresIn;
    private String role;
    private String branch;
}
