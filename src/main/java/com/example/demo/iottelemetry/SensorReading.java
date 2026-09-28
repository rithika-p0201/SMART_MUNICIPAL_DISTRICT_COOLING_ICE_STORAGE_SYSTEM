package com.example.demo.iottelemetry;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sensor_readings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sensorCode; // e.g. TE-CHW-SUP-01

    @Column(nullable = false)
    private String sensorType; // TEMPERATURE, FLOW_METER, PRESSURE, POWER_METER, ICE_LEVEL

    @Column(nullable = false)
    private Double readingValue;

    @Column(nullable = false)
    private String unit; // °C, m3/h, kPa, kW, %

    @Column(nullable = false)
    private String location; // e.g. Main Underground Cooling Ring

    private LocalDateTime recordedAt;

    @PrePersist
    protected void onCreate() {
        if (this.recordedAt == null) {
            this.recordedAt = LocalDateTime.now();
        }
    }
}
