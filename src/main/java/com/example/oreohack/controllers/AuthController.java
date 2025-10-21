package com.example.oreohack.controllers;

import com.example.oreohack.dto.request.LoginRequestDTO;
import com.example.oreohack.dto.request.RegisterRequestDTO;
import com.example.oreohack.dto.response.AuthResponseDTO;
import com.example.oreohack.dto.response.LoginResponseDTO;
import com.example.oreohack.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ==============================
    // 🧾 REGISTRO DE USUARIO
    // ==============================
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterRequestDTO request) {
        AuthResponseDTO response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ==============================
    // 🔑 LOGIN DE USUARIO
    // ==============================
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO response = authService.login(loginRequestDTO);
        return ResponseEntity.ok(response);
    }
}
