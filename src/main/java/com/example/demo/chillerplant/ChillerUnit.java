package com.example.demo.chillerplant;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chiller_units")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChillerUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String unitCode; // e.g., CH-01-CENT, CH-03-DUAL

    @Column(nullable = false)
    private String chillerType; // CENTRIFUGAL, DUAL_MODE_GLYCOL, ABSORPTION

    @Column(nullable = false)
    private Double ratedCoolingCapacityTR; // Capacity in Tons of Refrigeration

    @Column(nullable = false)
    private Double ratedElectricalPowerKW; // Full load electrical power draw

    @Column(nullable = false)
    private Double currentCoolingLoadTR; // Active cooling delivered

    @Column(nullable = false)
    private Double currentPowerConsumptionKW; // Active power draw

    @Column(nullable = false)
    private Double cop; // Coefficient of Performance (e.g. 5.8)

    @Column(nullable = false)
    private String status; // RUNNING, STANDBY, ICE_MODE, MAINTENANCE, FAULT

    @Column(nullable = false)
    private Double chilledWaterSetpointC; // e.g. 5.0°C (or -5.5°C in ice making mode)

    @Column(nullable = false)
    private Double evaporatorTempC;

    @Column(nullable = false)
    private Double condenserTempC;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = "STANDBY";
        if (this.currentCoolingLoadTR == null) this.currentCoolingLoadTR = 0.0;
        if (this.currentPowerConsumptionKW == null) this.currentPowerConsumptionKW = 0.0;
        if (this.cop == null) this.cop = 5.5;
        if (this.chilledWaterSetpointC == null) this.chilledWaterSetpointC = 5.0;
        if (this.evaporatorTempC == null) this.evaporatorTempC = 4.5;
        if (this.condenserTempC == null) this.condenserTempC = 32.0;
    }

    public double calculatePartLoadRatio() {
        if (ratedCoolingCapacityTR != null && ratedCoolingCapacityTR > 0) {
            return currentCoolingLoadTR / ratedCoolingCapacityTR;
        }
        return 0.0;
    }
}
