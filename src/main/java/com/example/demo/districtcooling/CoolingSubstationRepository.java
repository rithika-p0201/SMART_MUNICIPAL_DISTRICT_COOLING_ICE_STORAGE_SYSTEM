package com.example.demo.districtcooling;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CoolingSubstationRepository extends JpaRepository<CoolingSubstation, Long> {
    Optional<CoolingSubstation> findBySubstationCode(String substationCode);
    List<CoolingSubstation> findByZoneId(Long zoneId);
    List<CoolingSubstation> findByStatus(String status);
}
