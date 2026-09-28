package com.example.demo.districtcooling;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "municipal_zones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MunicipalZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String zoneName;

    @Column(nullable = false)
    private String zoneType; // e.g., HOSPITAL, COMMERCIAL, CIVIC_CENTER, RESIDENTIAL, EDUCATIONAL

    @Column(nullable = false)
    private Double peakDemandTR;

    @Column(nullable = false)
    private Double currentDemandTR;

    @Column(nullable = false)
    private Double supplyWaterTempC; // Typically 4.5°C - 6.0°C

    @Column(nullable = false)
    private Double returnWaterTempC; // Typically 12.0°C - 14.5°C

    @Column(nullable = false)
    private Double waterFlowRateM3h;

    @Column(nullable = false)
    private Double targetDeltaTC; // Target temperature difference (e.g. 8.0°C)

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.supplyWaterTempC == null) this.supplyWaterTempC = 5.5;
        if (this.returnWaterTempC == null) this.returnWaterTempC = 13.5;
        if (this.targetDeltaTC == null) this.targetDeltaTC = 8.0;
    }

    public double calculateActualDeltaT() {
        if (returnWaterTempC != null && supplyWaterTempC != null) {
            return returnWaterTempC - supplyWaterTempC;
        }
        return 0.0;
    }
}
