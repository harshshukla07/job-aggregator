package com.jobaggregator.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "processed_jobs")
@Data
@NoArgsConstructor
public class JobEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String atsJobId;

    private String company;
    private String title;

    private LocalDateTime processedAt;

    public JobEntity(String atsJobId, String company, String title) {
        this.atsJobId = atsJobId;
        this.company = company;
        this.title = title;
        this.processedAt = LocalDateTime.now();
    }
}