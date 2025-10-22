package com.example.oreohack.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class AuthResponseDTO {
    private String id;
    private String username;
    private String email;
    private String role;
    private String branch;
    private LocalDateTime createdAt;
}