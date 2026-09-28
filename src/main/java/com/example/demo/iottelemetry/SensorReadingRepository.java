package com.example.demo.iottelemetry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {
    List<SensorReading> findTop50ByOrderByRecordedAtDesc();
    List<SensorReading> findBySensorCodeOrderByRecordedAtDesc(String sensorCode);
    List<SensorReading> findBySensorType(String sensorType);
}
