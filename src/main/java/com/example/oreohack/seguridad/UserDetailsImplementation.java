package com.example.oreohack.seguridad;

import com.example.oreohack.entidades.UserClass;
import com.example.oreohack.repositorios.UserClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsImplementation implements UserDetailsService {
    private final UserClassRepository userRepository;

    //Sobrescribir funcion, UserDetailsService ya tiene esta misma función definida, nosotros personalizamos
    //Su funcionamiento
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserClass> user = userRepository.findByUsername(username);

        //Primero buscamos en estudiante
        if (user.isPresent()) {
            UserClass u = user.get();
            return new User(
                    u.getUsername(),
                    u.getPassword(),
                    Collections.singleton(new SimpleGrantedAuthority(u.getRole().name()))
            );
        }

        throw new UsernameNotFoundException("Usuario con email: "+username+" no encontrado ");
    }
}
