package com.example.demo.iottelemetry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TelemetryMonitoringService {

    private final SensorReadingRepository sensorRepository;
    private final SystemAlertRepository alertRepository;

    @Autowired
    public TelemetryMonitoringService(SensorReadingRepository sensorRepository, SystemAlertRepository alertRepository) {
        this.sensorRepository = sensorRepository;
        this.alertRepository = alertRepository;
    }

    public SensorReading recordReading(SensorReading reading) {
        SensorReading saved = sensorRepository.save(reading);
        // Automated Safety Rule Verification
        checkThresholdsAndRaiseAlerts(saved);
        return saved;
    }

    public List<SensorReading> getRecentReadings() {
        return sensorRepository.findTop50ByOrderByRecordedAtDesc();
    }

    public List<SystemAlert> getActiveAlerts() {
        return alertRepository.findByIsResolvedFalseOrderByTriggeredAtDesc();
    }

    public SystemAlert resolveAlert(Long alertId) {
        SystemAlert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found with id: " + alertId));
        alert.setIsResolved(true);
        return alertRepository.save(alert);
    }

    private void checkThresholdsAndRaiseAlerts(SensorReading reading) {
        if ("TEMPERATURE".equalsIgnoreCase(reading.getSensorType()) && reading.getReadingValue() > 18.0 && reading.getLocation().contains("Supply")) {
            SystemAlert alert = SystemAlert.builder()
                    .alertCode("ALT-TEMP-HIGH")
                    .severity("CRITICAL")
                    .component(reading.getLocation())
                    .description("Chilled water supply temperature critically high: " + reading.getReadingValue() + "°C (threshold: 12.0°C).")
                    .isResolved(false)
                    .build();
            alertRepository.save(alert);
        } else if ("PRESSURE".equalsIgnoreCase(reading.getSensorType()) && reading.getReadingValue() > 350.0) {
            SystemAlert alert = SystemAlert.builder()
                    .alertCode("ALT-PRESS-HIGH")
                    .severity("WARNING")
                    .component(reading.getLocation())
                    .description("High differential pressure spike detected: " + reading.getReadingValue() + " kPa.")
                    .isResolved(false)
                    .build();
            alertRepository.save(alert);
        }
    }
}
