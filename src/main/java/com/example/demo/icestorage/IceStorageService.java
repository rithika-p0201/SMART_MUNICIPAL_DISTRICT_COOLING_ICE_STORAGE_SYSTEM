package com.example.demo.icestorage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class IceStorageService {

    private final IceStorageTankRepository tankRepository;

    @Autowired
    public IceStorageService(IceStorageTankRepository tankRepository) {
        this.tankRepository = tankRepository;
    }

    public List<IceStorageTank> getAllTanks() {
        return tankRepository.findAll();
    }

    public Optional<IceStorageTank> getTankById(Long id) {
        return tankRepository.findById(id);
    }

    public IceStorageTank saveTank(IceStorageTank tank) {
        return tankRepository.save(tank);
    }

    public IceStorageTank startCharging(Long id, Double chargeRateTR) {
        IceStorageTank tank = tankRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ice storage tank not found with id: " + id));
        tank.setOperatingMode("CHARGING_FREEZE");
        tank.setGlycolInletTempC(-4.5);
        tank.setGlycolOutletTempC(-1.8);
        tank.setLastCycleTimestamp(LocalDateTime.now());
        // Simulating added charge
        double addedCapacity = (chargeRateTR != null ? chargeRateTR : tank.getMaxChargeRateTR()) * 1.0;
        double newInventory = Math.min(tank.getTotalCapacityTRHr(), tank.getCurrentIceInventoryTRHr() + addedCapacity);
        tank.setCurrentIceInventoryTRHr(newInventory);
        return tankRepository.save(tank);
    }

    public IceStorageTank startDischarging(Long id, Double dischargeRateTR) {
        IceStorageTank tank = tankRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ice storage tank not found with id: " + id));
        tank.setOperatingMode("DISCHARGING_MELT");
        tank.setGlycolInletTempC(10.5);
        tank.setGlycolOutletTempC(3.8);
        tank.setLastCycleTimestamp(LocalDateTime.now());
        // Simulating melted ice
        double meltedCapacity = (dischargeRateTR != null ? dischargeRateTR : tank.getMaxDischargeRateTR()) * 1.0;
        double newInventory = Math.max(0.0, tank.getCurrentIceInventoryTRHr() - meltedCapacity);
        tank.setCurrentIceInventoryTRHr(newInventory);
        return tankRepository.save(tank);
    }

    public IceStorageTank setIdle(Long id) {
        IceStorageTank tank = tankRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ice storage tank not found with id: " + id));
        tank.setOperatingMode("IDLE_STORAGE");
        tank.setGlycolInletTempC(0.0);
        tank.setGlycolOutletTempC(0.0);
        tank.setLastCycleTimestamp(LocalDateTime.now());
        return tankRepository.save(tank);
    }

    public Map<String, Object> getStorageStatus() {
        Map<String, Object> status = new HashMap<>();
        Double totalCap = tankRepository.sumTotalCapacityTRHr();
        Double currentInv = tankRepository.sumTotalCurrentInventoryTRHr();
        Double avgSOC = tankRepository.averageStateOfChargePct();

        status.put("totalCapacityTRHr", totalCap);
        status.put("currentIceInventoryTRHr", currentInv);
        status.put("averageStateOfChargePct", avgSOC);
        status.put("equivalentKWhThermal", currentInv * 3.51685);
        return status;
    }
}
