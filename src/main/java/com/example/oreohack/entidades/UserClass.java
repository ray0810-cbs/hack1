package com.example.oreohack.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserClass {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true, length = 30)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Role role;

    // Sucursal asignada (solo para ROLE_BRANCH)
    @Column(nullable = true)
    private String branch;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum Role {
        CENTRAL,
        BRANCH
    }
}
