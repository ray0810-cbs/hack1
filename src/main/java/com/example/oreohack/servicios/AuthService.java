package com.example.oreohack.servicios;

import com.example.oreohack.dto.request.AuthRequestDTO;
import com.example.oreohack.dto.request.RegisterRequestDTO;
import com.example.oreohack.dto.response.AuthResponseDTO;
import com.example.oreohack.dto.response.UserResponseDTO;
import com.example.oreohack.entidades.*;
import com.example.oreohack.entidades.roles.Role;
import com.example.oreohack.excepciones.*;
import com.example.oreohack.repositorios.*;
import com.example.oreohack.seguridad.JwtService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ModelMapper mapper;

    @Transactional
    public UserResponseDTO register(RegisterRequestDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername()))
            throw new ResourceConflictException("El username ya existe");

        if (userRepository.existsByEmail(dto.getEmail()))
            throw new ResourceConflictException("El email ya está registrado");

        Role role = Role.valueOf(dto.getRole().toUpperCase());
        Branch branch = null;

        if (role == Role.BRANCH) {
            if (dto.getBranch() == null)
                throw new InvalidRequestException("Debe especificar una sucursal para ROLE_BRANCH");
            branch = branchRepository.findByName(dto.getBranch())
                    .orElseGet(() -> branchRepository.save(
                            Branch.builder().name(dto.getBranch()).build()));
        }

        UserClass user = UserClass.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(role)
                .branch(branch)
                .createdAt(Instant.now())
                .build();

        userRepository.save(user);
        return mapper.map(user, UserResponseDTO.class);
    }

    public AuthResponseDTO authenticate(AuthRequestDTO dto) {
        UserClass user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword()))
            throw new UnauthorizedActionException("Credenciales inválidas");

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtService.generateToken(userDetails, user.getRole().name());
        return AuthResponseDTO.builder()
                .token(token)
                .expiresIn(jwtService.getExpirationTime())
                .role(user.getRole().name())
                .branch(user.getBranch() != null ? user.getBranch().getName() : null)
                .build();
    }
}

