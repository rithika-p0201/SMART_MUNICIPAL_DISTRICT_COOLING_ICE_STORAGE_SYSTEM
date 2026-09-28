package com.example.demo.energytariff;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tariff_rates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TariffRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String tierName; // OFF_PEAK_NIGHT, MID_PEAK_MORNING, ON_PEAK_AFTERNOON, MID_PEAK_EVENING

    @Column(nullable = false)
    private Integer startHour; // 0 to 23

    @Column(nullable = false)
    private Integer endHour; // 0 to 24

    @Column(nullable = false)
    private Double ratePerKWh; // Electricity price in $/kWh (e.g. $0.045 off-peak vs $0.285 on-peak)

    @Column(nullable = false)
    private Double demandChargePerKW; // Peak power demand charge ($/kW)

    @Column(nullable = false)
    private Boolean isActive;
}
