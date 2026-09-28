package com.example.demo.municipalbilling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/billing")
@CrossOrigin(origins = "*")
public class MunicipalBillingController {

    private final MunicipalBillingService billingService;

    @Autowired
    public MunicipalBillingController(MunicipalBillingService billingService) {
        this.billingService = billingService;
    }

    @GetMapping("/bills")
    public ResponseEntity<List<DistrictCoolingBill>> getAllBills() {
        return ResponseEntity.ok(billingService.getAllBills());
    }

    @GetMapping("/bills/zone/{zoneId}")
    public ResponseEntity<List<DistrictCoolingBill>> getBillsForZone(@PathVariable Long zoneId) {
        return ResponseEntity.ok(billingService.getBillsForZone(zoneId));
    }

    @PostMapping("/generate")
    public ResponseEntity<DistrictCoolingBill> generateBill(
            @RequestParam Long zoneId,
            @RequestParam Integer month,
            @RequestParam Integer year) {
        return ResponseEntity.ok(billingService.generateMonthlyBill(zoneId, month, year));
    }

    @GetMapping("/sustainability-metrics")
    public ResponseEntity<Map<String, Object>> getSustainabilityMetrics() {
        return ResponseEntity.ok(billingService.getSustainabilityMetrics());
    }
}
