package com.example.oreohack.controladores;

import com.example.oreohack.dto.request.AuthRequestDTO;
import com.example.oreohack.dto.request.RegisterRequestDTO;
import com.example.oreohack.dto.response.AuthResponseDTO;
import com.example.oreohack.dto.response.UserResponseDTO;
import com.example.oreohack.servicios.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody RegisterRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO dto) {
        return ResponseEntity.ok(authService.authenticate(dto));
    }
}


