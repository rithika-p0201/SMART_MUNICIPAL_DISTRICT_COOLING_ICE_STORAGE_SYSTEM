package com.example.demo.systemconfig;

import com.example.demo.chillerplant.ChillerUnit;
import com.example.demo.chillerplant.ChillerUnitRepository;
import com.example.demo.districtcooling.CoolingSubstation;
import com.example.demo.districtcooling.CoolingSubstationRepository;
import com.example.demo.districtcooling.MunicipalZone;
import com.example.demo.districtcooling.MunicipalZoneRepository;
import com.example.demo.energytariff.TariffRate;
import com.example.demo.energytariff.TariffRateRepository;
import com.example.demo.icestorage.IceStorageTank;
import com.example.demo.icestorage.IceStorageTankRepository;
import com.example.demo.iottelemetry.SensorReading;
import com.example.demo.iottelemetry.SensorReadingRepository;
import com.example.demo.iottelemetry.SystemAlert;
import com.example.demo.iottelemetry.SystemAlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final MunicipalZoneRepository zoneRepository;
    private final CoolingSubstationRepository substationRepository;
    private final ChillerUnitRepository chillerRepository;
    private final IceStorageTankRepository tankRepository;
    private final TariffRateRepository tariffRepository;
    private final SensorReadingRepository sensorRepository;
    private final SystemAlertRepository alertRepository;

    @Autowired
    public DataInitializer(
            MunicipalZoneRepository zoneRepository,
            CoolingSubstationRepository substationRepository,
            ChillerUnitRepository chillerRepository,
            IceStorageTankRepository tankRepository,
            TariffRateRepository tariffRepository,
            SensorReadingRepository sensorRepository,
            SystemAlertRepository alertRepository) {
        this.zoneRepository = zoneRepository;
        this.substationRepository = substationRepository;
        this.chillerRepository = chillerRepository;
        this.tankRepository = tankRepository;
        this.tariffRepository = tariffRepository;
        this.sensorRepository = sensorRepository;
        this.alertRepository = alertRepository;
    }

    @Override
    public void run(String... args) {
        if (zoneRepository.count() == 0) {
            initializeData();
        }
        printWebsiteBanner();
    }

    private void printWebsiteBanner() {
        System.out.println();
        System.out.println("====================================================================================================");
        System.out.println("  ❄️  SMART MUNICIPAL DISTRICT COOLING & ICE THERMAL ENERGY STORAGE (ITES) SYSTEM");
        System.out.println("====================================================================================================");
        System.out.println("  🚀 SYSTEM STATUS:     ONLINE & RUNNING");
        System.out.println("  🌐 YOUR WEBSITE LINK: http://localhost:8080");
        System.out.println("  📊 SCADA DASHBOARD:   http://localhost:8080/index.html");
        System.out.println("  🗄️ H2 DATABASE:       http://localhost:8080/h2-console");
        System.out.println("  🔌 REST API ROOT:     http://localhost:8080/api/district/zones");
        System.out.println("====================================================================================================");
        System.out.println("  👉 Open your browser and go to: http://localhost:8080");
        System.out.println("====================================================================================================");
        System.out.println();
    }

    private void initializeData() {
        // 1. Municipal Zones
        MunicipalZone hosp = zoneRepository.save(MunicipalZone.builder()
                .zoneName("Metropolitan General Hospital")
                .zoneType("HOSPITAL")
                .peakDemandTR(1800.0)
                .currentDemandTR(1450.0)
                .supplyWaterTempC(5.0)
                .returnWaterTempC(13.5)
                .waterFlowRateM3h(532.0)
                .targetDeltaTC(8.5)
                .build());

        MunicipalZone civic = zoneRepository.save(MunicipalZone.builder()
                .zoneName("Civic Government Center & Courts")
                .zoneType("CIVIC_CENTER")
                .peakDemandTR(1200.0)
                .currentDemandTR(850.0)
                .supplyWaterTempC(5.5)
                .returnWaterTempC(13.2)
                .waterFlowRateM3h(345.0)
                .targetDeltaTC(7.7)
                .build());

        MunicipalZone fin = zoneRepository.save(MunicipalZone.builder()
                .zoneName("Financial & Technology Tower Cluster")
                .zoneType("COMMERCIAL")
                .peakDemandTR(3500.0)
                .currentDemandTR(2900.0)
                .supplyWaterTempC(5.2)
                .returnWaterTempC(13.8)
                .waterFlowRateM3h(1080.0)
                .targetDeltaTC(8.6)
                .build());

        MunicipalZone res = zoneRepository.save(MunicipalZone.builder()
                .zoneName("Greenfield Municipal Residential Park")
                .zoneType("RESIDENTIAL")
                .peakDemandTR(2000.0)
                .currentDemandTR(1200.0)
                .supplyWaterTempC(6.0)
                .returnWaterTempC(13.0)
                .waterFlowRateM3h(480.0)
                .targetDeltaTC(7.0)
                .build());

        MunicipalZone univ = zoneRepository.save(MunicipalZone.builder()
                .zoneName("Municipal University Campus & Library")
                .zoneType("EDUCATIONAL")
                .peakDemandTR(1500.0)
                .currentDemandTR(950.0)
                .supplyWaterTempC(5.5)
                .returnWaterTempC(13.3)
                .waterFlowRateM3h(380.0)
                .targetDeltaTC(7.8)
                .build());

        // 2. Substations
        substationRepository.save(CoolingSubstation.builder()
                .substationCode("ETS-HOSP-01")
                .zone(hosp)
                .heatExchangerCapacityKW(6330.0)
                .controlValveOpeningPct(82.5)
                .differentialPressureKPa(160.0)
                .status("OPERATIONAL")
                .build());

        substationRepository.save(CoolingSubstation.builder()
                .substationCode("ETS-FIN-01")
                .zone(fin)
                .heatExchangerCapacityKW(7030.0)
                .controlValveOpeningPct(88.0)
                .differentialPressureKPa(172.0)
                .status("OPERATIONAL")
                .build());

        // 3. Chillers
        chillerRepository.save(ChillerUnit.builder()
                .unitCode("CH-01-CENT")
                .chillerType("CENTRIFUGAL")
                .ratedCoolingCapacityTR(2500.0)
                .ratedElectricalPowerKW(1500.0)
                .currentCoolingLoadTR(2200.0)
                .currentPowerConsumptionKW(1320.0)
                .cop(5.86)
                .status("RUNNING")
                .chilledWaterSetpointC(5.0)
                .evaporatorTempC(4.2)
                .condenserTempC(32.5)
                .build());

        chillerRepository.save(ChillerUnit.builder()
                .unitCode("CH-02-CENT")
                .chillerType("CENTRIFUGAL")
                .ratedCoolingCapacityTR(2500.0)
                .ratedElectricalPowerKW(1500.0)
                .currentCoolingLoadTR(2100.0)
                .currentPowerConsumptionKW(1280.0)
                .cop(5.77)
                .status("RUNNING")
                .chilledWaterSetpointC(5.0)
                .evaporatorTempC(4.3)
                .condenserTempC(32.8)
                .build());

        chillerRepository.save(ChillerUnit.builder()
                .unitCode("CH-03-DUAL")
                .chillerType("DUAL_MODE_GLYCOL")
                .ratedCoolingCapacityTR(2000.0)
                .ratedElectricalPowerKW(1350.0)
                .currentCoolingLoadTR(1800.0)
                .currentPowerConsumptionKW(1215.0)
                .cop(5.21)
                .status("RUNNING")
                .chilledWaterSetpointC(5.0)
                .evaporatorTempC(-4.5)
                .condenserTempC(33.0)
                .build());

        chillerRepository.save(ChillerUnit.builder()
                .unitCode("CH-04-DUAL")
                .chillerType("DUAL_MODE_GLYCOL")
                .ratedCoolingCapacityTR(2000.0)
                .ratedElectricalPowerKW(1350.0)
                .currentCoolingLoadTR(0.0)
                .currentPowerConsumptionKW(0.0)
                .cop(5.21)
                .status("STANDBY")
                .chilledWaterSetpointC(-5.5)
                .evaporatorTempC(-6.0)
                .condenserTempC(31.0)
                .build());

        // 4. Ice Storage Tanks
        tankRepository.save(IceStorageTank.builder()
                .tankCode("ITES-TANK-01")
                .tankName("East Quad Internal-Melt Ice Tank")
                .storageType("INTERNAL_MELT_ICE_ON_COIL")
                .totalCapacityTRHr(8000.0)
                .currentIceInventoryTRHr(6800.0)
                .stateOfChargePct(85.0)
                .glycolInletTempC(-3.8)
                .glycolOutletTempC(-1.2)
                .maxChargeRateTR(1200.0)
                .maxDischargeRateTR(1600.0)
                .operatingMode("DISCHARGING_MELT")
                .build());

        tankRepository.save(IceStorageTank.builder()
                .tankCode("ITES-TANK-02")
                .tankName("West Quad Internal-Melt Ice Tank")
                .storageType("INTERNAL_MELT_ICE_ON_COIL")
                .totalCapacityTRHr(8000.0)
                .currentIceInventoryTRHr(6400.0)
                .stateOfChargePct(80.0)
                .glycolInletTempC(-3.9)
                .glycolOutletTempC(-1.4)
                .maxChargeRateTR(1200.0)
                .maxDischargeRateTR(1600.0)
                .operatingMode("DISCHARGING_MELT")
                .build());

        tankRepository.save(IceStorageTank.builder()
                .tankCode("ITES-TANK-03")
                .tankName("Central Modular Ice Bank Alpha")
                .storageType("ENCAPSULATED_ICE")
                .totalCapacityTRHr(6000.0)
                .currentIceInventoryTRHr(5520.0)
                .stateOfChargePct(92.0)
                .glycolInletTempC(-4.2)
                .glycolOutletTempC(-1.8)
                .maxChargeRateTR(900.0)
                .maxDischargeRateTR(1200.0)
                .operatingMode("DISCHARGING_MELT")
                .build());

        // 5. TOU Tariffs
        tariffRepository.save(TariffRate.builder()
                .tierName("OFF_PEAK_NIGHT")
                .startHour(23)
                .endHour(7)
                .ratePerKWh(0.045)
                .demandChargePerKW(0.0)
                .isActive(true)
                .build());

        tariffRepository.save(TariffRate.builder()
                .tierName("MID_PEAK_MORNING")
                .startHour(7)
                .endHour(12)
                .ratePerKWh(0.115)
                .demandChargePerKW(8.50)
                .isActive(true)
                .build());

        tariffRepository.save(TariffRate.builder()
                .tierName("ON_PEAK_AFTERNOON")
                .startHour(12)
                .endHour(18)
                .ratePerKWh(0.285)
                .demandChargePerKW(24.00)
                .isActive(true)
                .build());

        tariffRepository.save(TariffRate.builder()
                .tierName("MID_PEAK_EVENING")
                .startHour(18)
                .endHour(23)
                .ratePerKWh(0.120)
                .demandChargePerKW(8.50)
                .isActive(true)
                .build());

        // 6. Sensor Readings
        sensorRepository.save(SensorReading.builder()
                .sensorCode("TE-CHW-SUP-01")
                .sensorType("TEMPERATURE")
                .readingValue(5.2)
                .unit("°C")
                .location("Central Chilled Water Primary Supply Header")
                .build());

        sensorRepository.save(SensorReading.builder()
                .sensorCode("TE-CHW-RET-01")
                .sensorType("TEMPERATURE")
                .readingValue(13.6)
                .unit("°C")
                .location("District Return Chilled Water Header")
                .build());

        sensorRepository.save(SensorReading.builder()
                .sensorCode("FE-CHW-TOT-01")
                .sensorType("FLOW_METER")
                .readingValue(2817.0)
                .unit("m3/h")
                .location("Main Underground Cooling Ring Distribution")
                .build());

        // 7. System Alerts
        alertRepository.save(SystemAlert.builder()
                .alertCode("ALT-DT-01")
                .severity("WARNING")
                .component("District Distribution Loop")
                .description("Low Delta-T syndrome detected at Greenfield Residential Substation (Delta-T = 5.2°C, below target 7.0°C).")
                .isResolved(false)
                .build());
    }
}
