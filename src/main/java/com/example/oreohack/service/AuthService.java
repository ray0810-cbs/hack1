package com.example.oreohack.service;

import com.example.oreohack.entidades.UserClass;
import com.example.oreohack.entidades.UserClass.Role;

import com.example.oreohack.repositorios.UserClassRepository;
import com.example.oreohack.seguridad.JwtService;
import com.example.oreohack.dto.request.RegisterRequestDTO;
import com.example.oreohack.dto.request.LoginRequestDTO;
import com.example.oreohack.dto.response.AuthResponseDTO;
import com.example.oreohack.dto.response.LoginResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserClassRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    // ==============================
    // 🧾 REGISTRO DE USUARIO
    // ==============================
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        // Validaciones básicas
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya existe.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado.");
        }
        if (request.getRole() == Role.BRANCH && (request.getBranch() == null || request.getBranch().isBlank())) {
            throw new RuntimeException("El campo 'branch' es obligatorio para usuarios BRANCH.");
        }
        if (request.getRole() == Role.CENTRAL) {
            request.setBranch(null);
        }

        // Crear y guardar usuario
        UserClass user = UserClass.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .branch(request.getBranch())
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        // Respuesta (sin token)
        return AuthResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .branch(user.getBranch())
                .createdAt(user.getCreatedAt())
                .build();
    }

    // ==============================
    // 🔑 LOGIN DE USUARIO
    // ==============================
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        String password = null;
        String rol = null;

        // Buscar usuario por username
        UserClass user = userRepository.findByUsername(loginRequestDTO.getUsername()).orElse(null);
        if (user != null) {
            password = user.getPassword();
            rol = user.getRole().name();
        } else {
            throw new UnknownError("Usuario con username " + loginRequestDTO.getUsername() + " no encontrado");
        }

        // Validar contraseña
        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), password)) {
            throw new UnknownError("Contraseña incorrecta");
        }

        // Generar token JWT
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequestDTO.getUsername());
        String token = jwtService.generateToken(userDetails, rol);

        return new LoginResponseDTO(token, jwtService.getExpirationTime(), rol, user.getBranch());
    }
}



