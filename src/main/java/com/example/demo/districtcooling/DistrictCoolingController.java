package com.example.demo.districtcooling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/district")
@CrossOrigin(origins = "*")
public class DistrictCoolingController {

    private final DistrictCoolingService districtCoolingService;

    @Autowired
    public DistrictCoolingController(DistrictCoolingService districtCoolingService) {
        this.districtCoolingService = districtCoolingService;
    }

    @GetMapping("/zones")
    public ResponseEntity<List<MunicipalZone>> getAllZones() {
        return ResponseEntity.ok(districtCoolingService.getAllZones());
    }

    @GetMapping("/zones/{id}")
    public ResponseEntity<MunicipalZone> getZoneById(@PathVariable Long id) {
        return districtCoolingService.getZoneById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/zones")
    public ResponseEntity<MunicipalZone> createZone(@RequestBody MunicipalZone zone) {
        return ResponseEntity.ok(districtCoolingService.saveZone(zone));
    }

    @PutMapping("/zones/{id}/demand")
    public ResponseEntity<MunicipalZone> updateDemand(
            @PathVariable Long id,
            @RequestParam Double demandTR) {
        return ResponseEntity.ok(districtCoolingService.updateZoneDemand(id, demandTR));
    }

    @GetMapping("/total-load")
    public ResponseEntity<Map<String, Object>> getTotalLoad() {
        Map<String, Object> response = new HashMap<>();
        response.put("totalCurrentDemandTR", districtCoolingService.getTotalCurrentDemandTR());
        response.put("totalPeakDemandTR", districtCoolingService.getTotalPeakDemandTR());
        response.put("totalCurrentDemandKW", districtCoolingService.getTotalCurrentDemandTR() * 3.51685);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/substations")
    public ResponseEntity<List<CoolingSubstation>> getAllSubstations() {
        return ResponseEntity.ok(districtCoolingService.getAllSubstations());
    }

    @PostMapping("/substations")
    public ResponseEntity<CoolingSubstation> createSubstation(@RequestBody CoolingSubstation substation) {
        return ResponseEntity.ok(districtCoolingService.saveSubstation(substation));
    }
}
