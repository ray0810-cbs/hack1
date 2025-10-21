package com.example.oreohack.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "report_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String branch;

    private LocalDate fromDate;
    private LocalDate toDate;

    @Column(nullable = false)
    private String emailTo;

    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum Status {
        PROCESSING,
        COMPLETED,
        FAILED
    }
}
