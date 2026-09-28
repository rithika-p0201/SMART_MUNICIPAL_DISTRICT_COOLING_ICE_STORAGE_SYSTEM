package com.example.demo.chillerplant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChillerUnitRepository extends JpaRepository<ChillerUnit, Long> {
    Optional<ChillerUnit> findByUnitCode(String unitCode);
    List<ChillerUnit> findByStatus(String status);
    List<ChillerUnit> findByChillerType(String chillerType);

    @Query("SELECT COALESCE(SUM(c.currentCoolingLoadTR), 0.0) FROM ChillerUnit c WHERE c.status = 'RUNNING' OR c.status = 'ICE_MODE'")
    Double sumTotalCoolingSuppliedTR();

    @Query("SELECT COALESCE(SUM(c.currentPowerConsumptionKW), 0.0) FROM ChillerUnit c WHERE c.status = 'RUNNING' OR c.status = 'ICE_MODE'")
    Double sumTotalPowerConsumptionKW();
}
