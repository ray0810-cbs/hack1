package com.example.oreohack.repositorios;

import com.example.oreohack.entidades.UserClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserClass, String> {
    Optional<UserClass> findByUsername(String username);
    Optional<UserClass> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}

