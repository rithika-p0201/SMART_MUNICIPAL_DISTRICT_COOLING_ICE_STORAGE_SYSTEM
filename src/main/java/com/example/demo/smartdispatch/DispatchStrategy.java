package com.example.demo.smartdispatch;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "dispatch_strategies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DispatchStrategy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;

    @Column(nullable = false)
    private Double totalDistrictDemandTR;

    @Column(nullable = false)
    private Double chillerCoolingSuppliedTR;

    @Column(nullable = false)
    private Double iceDischargeCoolingTR;

    @Column(nullable = false)
    private Double gridElectricalPowerKW;

    @Column(nullable = false)
    private String operatingMode; // OFF_PEAK_ICE_BUILDING, PEAK_SHAVING_DISCHARGE, CHILLER_DIRECT, HYBRID_COOLING

    @Column(nullable = false)
    private Double hourlyEnergyCostUSD;

    @Column(nullable = false)
    private Double baselineCostWithoutStorageUSD;

    @Column(nullable = false)
    private Double hourlySavingsUSD;

    @PrePersist
    protected void onCreate() {
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
        if (this.hourlySavingsUSD == null && this.baselineCostWithoutStorageUSD != null && this.hourlyEnergyCostUSD != null) {
            this.hourlySavingsUSD = Math.max(0.0, this.baselineCostWithoutStorageUSD - this.hourlyEnergyCostUSD);
        }
    }
}
