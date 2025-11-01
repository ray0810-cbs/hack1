package com.example.oreohack.entidades;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "branches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL)
    private List<UserClass> users;

    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL)
    private List<Sale> sales;
}
