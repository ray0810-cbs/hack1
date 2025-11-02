package com.example.oreohack.seguridad;

import com.example.oreohack.entidades.UserClass;
import com.example.oreohack.repositorios.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsImplementation implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserClass user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario con username: " + username + " no encontrado"));
        return user; // <-- devuelve directamente la entidad
    }
}

