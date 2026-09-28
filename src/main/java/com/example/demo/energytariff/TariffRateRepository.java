package com.example.demo.energytariff;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TariffRateRepository extends JpaRepository<TariffRate, Long> {
    Optional<TariffRate> findByTierName(String tierName);
    List<TariffRate> findByIsActiveTrue();

    @Query("SELECT t FROM TariffRate t WHERE t.isActive = true AND " +
           "((t.startHour <= t.endHour AND :hour >= t.startHour AND :hour < t.endHour) OR " +
           "(t.startHour > t.endHour AND (:hour >= t.startHour OR :hour < t.endHour)))")
    Optional<TariffRate> findActiveTariffForHour(@Param("hour") int hour);
}
