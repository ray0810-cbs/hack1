package com.example.oreohack.repositorios;

import com.example.oreohack.entidades.UserClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserClass, Long> {
    Optional<UserClass> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}

