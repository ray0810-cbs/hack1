package com.example.oreohack.dto.response;

import lombok.*;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private String id;
    private String username;
    private String email;
    private String role;
    private String branch;
    private Instant createdAt;
}

