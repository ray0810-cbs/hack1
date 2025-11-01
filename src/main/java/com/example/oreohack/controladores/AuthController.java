package com.example.oreohack.controladores;

import com.example.oreohack.dto.request.AuthRequestDTO;
import com.example.oreohack.dto.request.RegisterRequestDTO;
import com.example.oreohack.dto.response.AuthResponseDTO;
import com.example.oreohack.dto.response.UserResponseDTO;
import com.example.oreohack.servicios.AuthService;
import jakarta.validation.Valid;
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
    @ResponseStatus(HttpStatus.CREATED) // 201
    public UserResponseDTO register(@Valid @RequestBody RegisterRequestDTO dto) {
        return authService.register(dto);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO dto) {
        AuthResponseDTO response = authService.authenticate(dto);
        return ResponseEntity.ok(response); // 200 OK
    }
}

