package com.example.demo.municipalbilling;

import com.example.demo.districtcooling.MunicipalZone;
import com.example.demo.districtcooling.MunicipalZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class MunicipalBillingService {

    private final DistrictCoolingBillRepository billRepository;
    private final MunicipalZoneRepository zoneRepository;

    @Autowired
    public MunicipalBillingService(DistrictCoolingBillRepository billRepository, MunicipalZoneRepository zoneRepository) {
        this.billRepository = billRepository;
        this.zoneRepository = zoneRepository;
    }

    public List<DistrictCoolingBill> getAllBills() {
        return billRepository.findAll();
    }

    public List<DistrictCoolingBill> getBillsForZone(Long zoneId) {
        return billRepository.findByZoneId(zoneId);
    }

    public DistrictCoolingBill generateMonthlyBill(Long zoneId, Integer month, Integer year) {
        MunicipalZone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new RuntimeException("Municipal Zone not found with id: " + zoneId));

        // Calculation: 30 days * 16 operating hours * current load
        double hoursPerMonth = 480.0;
        double tonHours = zone.getCurrentDemandTR() * hoursPerMonth;
        double coolingRatePerTonHr = 0.18; // District cooling delivery tariff ($/TR-hr)
        double energyCharge = tonHours * coolingRatePerTonHr;

        // Demand charge based on peak load
        double demandRatePerTR = 12.50;
        double peakDemandCharge = zone.getPeakDemandTR() * demandRatePerTR;
        double totalBill = energyCharge + peakDemandCharge;

        // Environmental sustainability metrics:
        // Peak demand avoided by ITES (approx 35% of peak)
        double peakAvoided = zone.getPeakDemandTR() * 0.35 * 3.51685; // in kW
        // Carbon emission offset: 0.52 kg CO2 per kWh shifted from peak gas turbines to night base load
        double carbonOffsetKg = (tonHours * 0.40 * 3.51685) * 0.18;

        DistrictCoolingBill bill = DistrictCoolingBill.builder()
                .zone(zone)
                .billingMonth(month)
                .billingYear(year)
                .totalTonHoursConsumed(tonHours)
                .energyChargeUSD(energyCharge)
                .peakDemandChargeUSD(peakDemandCharge)
                .totalBillAmountUSD(totalBill)
                .peakDemandAvoidedKW(peakAvoided)
                .carbonReductionKgCO2(carbonOffsetKg)
                .paymentStatus("PENDING")
                .build();

        return billRepository.save(bill);
    }

    public Map<String, Object> getSustainabilityMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        Double totalCarbonKg = billRepository.sumTotalCarbonReductionKgCO2();
        Double totalPeakAvoidedKW = billRepository.sumTotalPeakDemandAvoidedKW();

        metrics.put("totalCarbonReductionKgCO2", totalCarbonKg);
        metrics.put("totalCarbonReductionMetricTons", totalCarbonKg / 1000.0);
        metrics.put("totalPeakDemandAvoidedKW", totalPeakAvoidedKW);
        metrics.put("totalPeakDemandAvoidedMW", totalPeakAvoidedKW / 1000.0);
        return metrics;
    }
}
