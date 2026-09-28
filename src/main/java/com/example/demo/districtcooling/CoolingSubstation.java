package com.example.demo.districtcooling;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cooling_substations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoolingSubstation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String substationCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "zone_id")
    private MunicipalZone zone;

    @Column(nullable = false)
    private Double heatExchangerCapacityKW;

    @Column(nullable = false)
    private Double controlValveOpeningPct; // 0.0 to 100.0%

    @Column(nullable = false)
    private Double differentialPressureKPa; // Target differential pressure

    @Column(nullable = false)
    private String status; // OPERATIONAL, BYPASSED, MAINTENANCE
}
