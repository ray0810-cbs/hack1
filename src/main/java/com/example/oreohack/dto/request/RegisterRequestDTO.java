package com.example.oreohack.dto.request;

import lombok.Data;
import com.example.oreohack.entidades.UserClass.Role;

@Data
public class RegisterRequestDTO {
    private String username;
    private String email;
    private String password;
    private Role role;
    private String branch;
}