package com.example.demo.smartdispatch;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DispatchStrategyRepository extends JpaRepository<DispatchStrategy, Long> {
    List<DispatchStrategy> findTop24ByOrderByTimestampDesc();

    @Query("SELECT COALESCE(SUM(d.hourlySavingsUSD), 0.0) FROM DispatchStrategy d")
    Double sumTotalSavingsUSD();
}
