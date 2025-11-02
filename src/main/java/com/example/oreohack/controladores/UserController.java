package com.example.oreohack.controladores;

import com.example.oreohack.dto.response.UserResponseDTO;
import com.example.oreohack.entidades.UserClass;
import com.example.oreohack.entidades.roles.Role;
import com.example.oreohack.excepciones.ForbiddenActionException;
import com.example.oreohack.repositorios.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final ModelMapper mapper;

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAll(@AuthenticationPrincipal UserClass user) {
        if (user.getRole() != Role.CENTRAL)
            throw new ForbiddenActionException("Solo ROLE_CENTRAL puede listar usuarios");

        List<UserResponseDTO> list = userRepository.findAll().stream()
                .map(u -> mapper.map(u, UserResponseDTO.class))
                .toList();

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getById(@PathVariable Long id,
                                                   @AuthenticationPrincipal UserClass user) {
        if (user.getRole() != Role.CENTRAL)
            throw new ForbiddenActionException("Solo ROLE_CENTRAL puede ver detalles de usuarios");

        UserClass found = userRepository.findById(id)
                .orElseThrow(() -> new com.example.oreohack.excepciones.ResourceNotFoundException("Usuario no encontrado"));
        return ResponseEntity.ok(mapper.map(found, UserResponseDTO.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal UserClass user) {
        if (user.getRole() != Role.CENTRAL)
            throw new ForbiddenActionException("Solo ROLE_CENTRAL puede eliminar usuarios");

        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
