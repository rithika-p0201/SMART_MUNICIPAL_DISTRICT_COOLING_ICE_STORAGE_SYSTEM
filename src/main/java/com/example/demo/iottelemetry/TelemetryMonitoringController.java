package com.example.demo.iottelemetry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/telemetry")
@CrossOrigin(origins = "*")
public class TelemetryMonitoringController {

    private final TelemetryMonitoringService telemetryService;

    @Autowired
    public TelemetryMonitoringController(TelemetryMonitoringService telemetryService) {
        this.telemetryService = telemetryService;
    }

    @GetMapping("/readings")
    public ResponseEntity<List<SensorReading>> getRecentReadings() {
        return ResponseEntity.ok(telemetryService.getRecentReadings());
    }

    @PostMapping("/record")
    public ResponseEntity<SensorReading> recordReading(@RequestBody SensorReading reading) {
        return ResponseEntity.ok(telemetryService.recordReading(reading));
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<SystemAlert>> getActiveAlerts() {
        return ResponseEntity.ok(telemetryService.getActiveAlerts());
    }

    @PutMapping("/alerts/{id}/resolve")
    public ResponseEntity<SystemAlert> resolveAlert(@PathVariable Long id) {
        return ResponseEntity.ok(telemetryService.resolveAlert(id));
    }
}
