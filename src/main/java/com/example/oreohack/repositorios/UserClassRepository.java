package com.example.oreohack.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.oreohack.entidades.UserClass;
import java.util.Optional;

@Repository
public interface UserClassRepository extends JpaRepository<UserClass, String> {

    Optional<UserClass> findByUsername(String username);

    Optional<UserClass> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}

