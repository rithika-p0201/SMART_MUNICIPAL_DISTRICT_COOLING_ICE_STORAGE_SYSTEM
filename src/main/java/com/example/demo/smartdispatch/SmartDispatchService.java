package com.example.demo.smartdispatch;

import com.example.demo.chillerplant.ChillerPlantService;
import com.example.demo.districtcooling.DistrictCoolingService;
import com.example.demo.energytariff.EnergyTariffService;
import com.example.demo.energytariff.TariffRate;
import com.example.demo.icestorage.IceStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SmartDispatchService {

    private final DispatchStrategyRepository dispatchRepository;
    private final DistrictCoolingService districtCoolingService;
    private final ChillerPlantService chillerPlantService;
    private final IceStorageService iceStorageService;
    private final EnergyTariffService energyTariffService;

    @Autowired
    public SmartDispatchService(
            DispatchStrategyRepository dispatchRepository,
            DistrictCoolingService districtCoolingService,
            ChillerPlantService chillerPlantService,
            IceStorageService iceStorageService,
            EnergyTariffService energyTariffService) {
        this.dispatchRepository = dispatchRepository;
        this.districtCoolingService = districtCoolingService;
        this.chillerPlantService = chillerPlantService;
        this.iceStorageService = iceStorageService;
        this.energyTariffService = energyTariffService;
    }

    public Map<String, Object> calculateOptimalDispatchPlan() {
        Double totalDemandTR = districtCoolingService.getTotalCurrentDemandTR();
        if (totalDemandTR == null || totalDemandTR <= 0.0) {
            totalDemandTR = 6500.0; // fallback realistic base load
        }

        int currentHour = LocalTime.now().getHour();
        TariffRate tariff = energyTariffService.getActiveTariffForCurrentTime()
                .orElse(TariffRate.builder()
                        .tierName("MID_PEAK")
                        .ratePerKWh(0.12)
                        .demandChargePerKW(8.5)
                        .build());

        String mode;
        double chillerTR;
        double iceTR;
        double chillerKwPerTr = 0.62; // Standard centrifugal chiller efficiency

        if ("OFF_PEAK_NIGHT".equalsIgnoreCase(tariff.getTierName()) || (currentHour >= 23 || currentHour < 7)) {
            mode = "OFF_PEAK_ICE_BUILDING";
            chillerTR = totalDemandTR + 2000.0; // Extra chiller capacity dedicated to freezing ice
            iceTR = 0.0;
        } else if ("ON_PEAK_AFTERNOON".equalsIgnoreCase(tariff.getTierName()) || (currentHour >= 12 && currentHour < 18)) {
            mode = "PEAK_SHAVING_DISCHARGE";
            iceTR = Math.min(3000.0, totalDemandTR * 0.45); // ITES supplies up to 45% of peak cooling
            chillerTR = totalDemandTR - iceTR; // Chillers only need to supply the remainder
        } else {
            mode = "HYBRID_COOLING";
            iceTR = Math.min(1000.0, totalDemandTR * 0.15);
            chillerTR = totalDemandTR - iceTR;
        }

        double gridPowerKW = chillerTR * chillerKwPerTr;
        double baselinePowerKW = totalDemandTR * chillerKwPerTr;

        double hourlyCost = gridPowerKW * tariff.getRatePerKWh();
        double baselineCost = baselinePowerKW * tariff.getRatePerKWh();
        double savings = Math.max(0.0, baselineCost - hourlyCost);

        Map<String, Object> plan = new HashMap<>();
        plan.put("currentHour", currentHour);
        plan.put("activeTariffTier", tariff.getTierName());
        plan.put("tariffRatePerKWh", tariff.getRatePerKWh());
        plan.put("totalDemandTR", totalDemandTR);
        plan.put("recommendedMode", mode);
        plan.put("chillerCoolingTR", chillerTR);
        plan.put("iceMeltCoolingTR", iceTR);
        plan.put("optimizedGridPowerKW", gridPowerKW);
        plan.put("baselineGridPowerKW", baselinePowerKW);
        plan.put("hourlyCostUSD", hourlyCost);
        plan.put("baselineCostUSD", baselineCost);
        plan.put("hourlySavingsUSD", savings);

        return plan;
    }

    public DispatchStrategy executeAndRecordDispatch() {
        Map<String, Object> plan = calculateOptimalDispatchPlan();

        DispatchStrategy strategy = DispatchStrategy.builder()
                .timestamp(LocalDateTime.now())
                .totalDistrictDemandTR((Double) plan.get("totalDemandTR"))
                .chillerCoolingSuppliedTR((Double) plan.get("chillerCoolingTR"))
                .iceDischargeCoolingTR((Double) plan.get("iceMeltCoolingTR"))
                .gridElectricalPowerKW((Double) plan.get("optimizedGridPowerKW"))
                .operatingMode((String) plan.get("recommendedMode"))
                .hourlyEnergyCostUSD((Double) plan.get("hourlyCostUSD"))
                .baselineCostWithoutStorageUSD((Double) plan.get("baselineCostUSD"))
                .hourlySavingsUSD((Double) plan.get("hourlySavingsUSD"))
                .build();

        return dispatchRepository.save(strategy);
    }

    public List<DispatchStrategy> getRecentDispatchHistory() {
        return dispatchRepository.findTop24ByOrderByTimestampDesc();
    }

    public Double getTotalCumulativeSavingsUSD() {
        return dispatchRepository.sumTotalSavingsUSD();
    }
}
