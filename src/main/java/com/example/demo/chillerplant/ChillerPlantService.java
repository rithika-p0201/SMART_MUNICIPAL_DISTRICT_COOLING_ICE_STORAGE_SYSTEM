package com.example.demo.chillerplant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ChillerPlantService {

    private final ChillerUnitRepository chillerRepository;

    @Autowired
    public ChillerPlantService(ChillerUnitRepository chillerRepository) {
        this.chillerRepository = chillerRepository;
    }

    public List<ChillerUnit> getAllChillers() {
        return chillerRepository.findAll();
    }

    public Optional<ChillerUnit> getChillerById(Long id) {
        return chillerRepository.findById(id);
    }

    public ChillerUnit saveChiller(ChillerUnit chiller) {
        return chillerRepository.save(chiller);
    }

    public ChillerUnit updateOperatingMode(Long id, String status) {
        ChillerUnit chiller = chillerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chiller not found with id: " + id));
        chiller.setStatus(status.toUpperCase());
        if ("STANDBY".equalsIgnoreCase(status) || "MAINTENANCE".equalsIgnoreCase(status)) {
            chiller.setCurrentCoolingLoadTR(0.0);
            chiller.setCurrentPowerConsumptionKW(0.0);
        } else if ("ICE_MODE".equalsIgnoreCase(status)) {
            chiller.setChilledWaterSetpointC(-5.5);
            chiller.setCurrentCoolingLoadTR(chiller.getRatedCoolingCapacityTR() * 0.90);
            chiller.setCurrentPowerConsumptionKW(chiller.getRatedElectricalPowerKW() * 0.95);
        } else if ("RUNNING".equalsIgnoreCase(status)) {
            chiller.setChilledWaterSetpointC(5.0);
            chiller.setCurrentCoolingLoadTR(chiller.getRatedCoolingCapacityTR() * 0.85);
            chiller.setCurrentPowerConsumptionKW(chiller.getRatedElectricalPowerKW() * 0.82);
        }
        return chillerRepository.save(chiller);
    }

    public ChillerUnit updateSetpoint(Long id, Double setpointC) {
        ChillerUnit chiller = chillerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chiller not found with id: " + id));
        chiller.setChilledWaterSetpointC(setpointC);
        return chillerRepository.save(chiller);
    }

    public Double getTotalCoolingSuppliedTR() {
        return chillerRepository.sumTotalCoolingSuppliedTR();
    }

    public Double getTotalPowerConsumptionKW() {
        return chillerRepository.sumTotalPowerConsumptionKW();
    }
}
