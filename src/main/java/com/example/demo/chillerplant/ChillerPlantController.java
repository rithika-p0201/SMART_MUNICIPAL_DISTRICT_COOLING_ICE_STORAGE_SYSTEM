package com.example.demo.chillerplant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chillers")
@CrossOrigin(origins = "*")
public class ChillerPlantController {

    private final ChillerPlantService chillerPlantService;

    @Autowired
    public ChillerPlantController(ChillerPlantService chillerPlantService) {
        this.chillerPlantService = chillerPlantService;
    }

    @GetMapping
    public ResponseEntity<List<ChillerUnit>> getAllChillers() {
        return ResponseEntity.ok(chillerPlantService.getAllChillers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChillerUnit> getChillerById(@PathVariable Long id) {
        return chillerPlantService.getChillerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ChillerUnit> createChiller(@RequestBody ChillerUnit chiller) {
        return ResponseEntity.ok(chillerPlantService.saveChiller(chiller));
    }

    @PutMapping("/{id}/mode")
    public ResponseEntity<ChillerUnit> updateMode(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(chillerPlantService.updateOperatingMode(id, status));
    }

    @PutMapping("/{id}/setpoint")
    public ResponseEntity<ChillerUnit> updateSetpoint(
            @PathVariable Long id,
            @RequestParam Double tempC) {
        return ResponseEntity.ok(chillerPlantService.updateSetpoint(id, tempC));
    }

    @GetMapping("/total-power")
    public ResponseEntity<Map<String, Object>> getTotalPowerAndCooling() {
        Map<String, Object> response = new HashMap<>();
        Double coolingTR = chillerPlantService.getTotalCoolingSuppliedTR();
        Double powerKW = chillerPlantService.getTotalPowerConsumptionKW();
        response.put("totalCoolingSuppliedTR", coolingTR);
        response.put("totalPowerConsumptionKW", powerKW);
        response.put("plantKwPerTr", (coolingTR > 0) ? (powerKW / coolingTR) : 0.0);
        return ResponseEntity.ok(response);
    }
}
