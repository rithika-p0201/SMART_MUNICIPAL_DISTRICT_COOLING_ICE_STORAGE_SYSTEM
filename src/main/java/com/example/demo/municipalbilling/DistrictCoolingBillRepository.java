package com.example.demo.municipalbilling;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistrictCoolingBillRepository extends JpaRepository<DistrictCoolingBill, Long> {
    List<DistrictCoolingBill> findByZoneId(Long zoneId);
    List<DistrictCoolingBill> findByBillingMonthAndBillingYear(Integer billingMonth, Integer billingYear);

    @Query("SELECT COALESCE(SUM(b.carbonReductionKgCO2), 0.0) FROM DistrictCoolingBill b")
    Double sumTotalCarbonReductionKgCO2();

    @Query("SELECT COALESCE(SUM(b.peakDemandAvoidedKW), 0.0) FROM DistrictCoolingBill b")
    Double sumTotalPeakDemandAvoidedKW();
}
