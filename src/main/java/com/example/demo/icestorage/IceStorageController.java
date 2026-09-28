package com.example.demo.icestorage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ice-storage")
@CrossOrigin(origins = "*")
public class IceStorageController {

    private final IceStorageService iceStorageService;

    @Autowired
    public IceStorageController(IceStorageService iceStorageService) {
        this.iceStorageService = iceStorageService;
    }

    @GetMapping("/tanks")
    public ResponseEntity<List<IceStorageTank>> getAllTanks() {
        return ResponseEntity.ok(iceStorageService.getAllTanks());
    }

    @GetMapping("/tanks/{id}")
    public ResponseEntity<IceStorageTank> getTankById(@PathVariable Long id) {
        return iceStorageService.getTankById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/tanks")
    public ResponseEntity<IceStorageTank> createTank(@RequestBody IceStorageTank tank) {
        return ResponseEntity.ok(iceStorageService.saveTank(tank));
    }

    @PostMapping("/tanks/{id}/charge")
    public ResponseEntity<IceStorageTank> chargeTank(
            @PathVariable Long id,
            @RequestParam(required = false) Double rateTR) {
        return ResponseEntity.ok(iceStorageService.startCharging(id, rateTR));
    }

    @PostMapping("/tanks/{id}/discharge")
    public ResponseEntity<IceStorageTank> dischargeTank(
            @PathVariable Long id,
            @RequestParam(required = false) Double rateTR) {
        return ResponseEntity.ok(iceStorageService.startDischarging(id, rateTR));
    }

    @PostMapping("/tanks/{id}/standby")
    public ResponseEntity<IceStorageTank> setStandby(@PathVariable Long id) {
        return ResponseEntity.ok(iceStorageService.setIdle(id));
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStorageStatus() {
        return ResponseEntity.ok(iceStorageService.getStorageStatus());
    }
}
