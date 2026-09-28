package com.example.demo.municipalbilling;

import com.example.demo.districtcooling.MunicipalZone;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "district_cooling_bills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DistrictCoolingBill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "zone_id", nullable = false)
    private MunicipalZone zone;

    @Column(nullable = false)
    private Integer billingMonth; // 1 to 12

    @Column(nullable = false)
    private Integer billingYear; // e.g. 2026

    @Column(nullable = false)
    private Double totalTonHoursConsumed;

    @Column(nullable = false)
    private Double energyChargeUSD;

    @Column(nullable = false)
    private Double peakDemandChargeUSD;

    @Column(nullable = false)
    private Double totalBillAmountUSD;

    @Column(nullable = false)
    private Double peakDemandAvoidedKW; // Grid peak shaved by ITES

    @Column(nullable = false)
    private Double carbonReductionKgCO2; // Carbon offset achieved

    @Column(nullable = false)
    private String paymentStatus; // PENDING, PAID, OVERDUE

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.paymentStatus == null) {
            this.paymentStatus = "PENDING";
        }
    }
}
