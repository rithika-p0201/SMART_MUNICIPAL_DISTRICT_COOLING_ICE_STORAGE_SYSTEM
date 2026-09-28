package com.example.demo.iottelemetry;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "system_alerts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String alertCode;

    @Column(nullable = false)
    private String severity; // INFO, WARNING, CRITICAL

    @Column(nullable = false)
    private String component;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    private LocalDateTime triggeredAt;

    @Column(nullable = false)
    private Boolean isResolved;

    @PrePersist
    protected void onCreate() {
        if (this.triggeredAt == null) {
            this.triggeredAt = LocalDateTime.now();
        }
        if (this.isResolved == null) {
            this.isResolved = false;
        }
    }
}
