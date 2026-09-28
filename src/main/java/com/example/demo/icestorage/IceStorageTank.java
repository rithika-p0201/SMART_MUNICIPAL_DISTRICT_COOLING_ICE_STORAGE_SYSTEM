package com.example.demo.icestorage;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ice_storage_tanks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IceStorageTank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String tankCode; // e.g. ITES-TANK-01

    @Column(nullable = false)
    private String tankName;

    @Column(nullable = false)
    private String storageType; // INTERNAL_MELT_ICE_ON_COIL, HARVEST_ICE, ENCAPSULATED_ICE

    @Column(nullable = false)
    private Double totalCapacityTRHr; // Total thermal energy storage capacity in Ton-Hours

    @Column(nullable = false)
    private Double currentIceInventoryTRHr; // Current latent energy stored in Ton-Hours

    @Column(nullable = false)
    private Double stateOfChargePct; // SOC 0.0% to 100.0%

    @Column(nullable = false)
    private Double glycolInletTempC; // e.g., -4.0°C during charge, +10.0°C during melt

    @Column(nullable = false)
    private Double glycolOutletTempC; // e.g., -1.5°C during charge, +3.5°C during melt

    @Column(nullable = false)
    private Double maxChargeRateTR; // Max rate of ice creation (TR)

    @Column(nullable = false)
    private Double maxDischargeRateTR; // Max rate of ice melt cooling delivery (TR)

    @Column(nullable = false)
    private String operatingMode; // CHARGING_FREEZE, DISCHARGING_MELT, IDLE_STORAGE, DEFROST

    private LocalDateTime lastCycleTimestamp;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        if (this.lastCycleTimestamp == null) {
            this.lastCycleTimestamp = LocalDateTime.now();
        }
        if (this.totalCapacityTRHr != null && this.totalCapacityTRHr > 0 && this.currentIceInventoryTRHr != null) {
            this.stateOfChargePct = Math.min(100.0, Math.max(0.0, (this.currentIceInventoryTRHr / this.totalCapacityTRHr) * 100.0));
        }
    }
}
