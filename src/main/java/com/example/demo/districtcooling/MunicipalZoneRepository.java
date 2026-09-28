package com.example.demo.districtcooling;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MunicipalZoneRepository extends JpaRepository<MunicipalZone, Long> {
    Optional<MunicipalZone> findByZoneName(String zoneName);

    @Query("SELECT COALESCE(SUM(z.currentDemandTR), 0.0) FROM MunicipalZone z")
    Double sumTotalCurrentDemandTR();

    @Query("SELECT COALESCE(SUM(z.peakDemandTR), 0.0) FROM MunicipalZone z")
    Double sumTotalPeakDemandTR();
}
