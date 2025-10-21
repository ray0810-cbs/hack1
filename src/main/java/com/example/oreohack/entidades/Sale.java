package com.example.oreohack.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "sales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String sku;

    @Column(nullable = false)
    private Integer units;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private String branch;

    @Column(nullable = false)
    private LocalDateTime soldAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // Usuario que creó la venta
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private UserClass createdBy;
}

