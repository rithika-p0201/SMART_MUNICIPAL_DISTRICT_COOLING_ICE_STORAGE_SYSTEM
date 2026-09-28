package com.example.demo.icestorage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IceStorageTankRepository extends JpaRepository<IceStorageTank, Long> {
    Optional<IceStorageTank> findByTankCode(String tankCode);
    List<IceStorageTank> findByOperatingMode(String operatingMode);

    @Query("SELECT COALESCE(SUM(t.totalCapacityTRHr), 0.0) FROM IceStorageTank t")
    Double sumTotalCapacityTRHr();

    @Query("SELECT COALESCE(SUM(t.currentIceInventoryTRHr), 0.0) FROM IceStorageTank t")
    Double sumTotalCurrentInventoryTRHr();

    @Query("SELECT COALESCE(AVG(t.stateOfChargePct), 0.0) FROM IceStorageTank t")
    Double averageStateOfChargePct();
}
