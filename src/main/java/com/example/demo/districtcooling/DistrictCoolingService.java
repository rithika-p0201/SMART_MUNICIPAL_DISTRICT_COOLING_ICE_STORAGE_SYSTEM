package com.example.demo.districtcooling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DistrictCoolingService {

    private final MunicipalZoneRepository zoneRepository;
    private final CoolingSubstationRepository substationRepository;

    @Autowired
    public DistrictCoolingService(MunicipalZoneRepository zoneRepository, CoolingSubstationRepository substationRepository) {
        this.zoneRepository = zoneRepository;
        this.substationRepository = substationRepository;
    }

    public List<MunicipalZone> getAllZones() {
        return zoneRepository.findAll();
    }

    public Optional<MunicipalZone> getZoneById(Long id) {
        return zoneRepository.findById(id);
    }

    public MunicipalZone saveZone(MunicipalZone zone) {
        return zoneRepository.save(zone);
    }

    public MunicipalZone updateZoneDemand(Long id, Double newDemandTR) {
        MunicipalZone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Municipal Zone not found with id: " + id));
        zone.setCurrentDemandTR(newDemandTR);
        return zoneRepository.save(zone);
    }

    public Double getTotalCurrentDemandTR() {
        return zoneRepository.sumTotalCurrentDemandTR();
    }

    public Double getTotalPeakDemandTR() {
        return zoneRepository.sumTotalPeakDemandTR();
    }

    public List<CoolingSubstation> getAllSubstations() {
        return substationRepository.findAll();
    }

    public CoolingSubstation saveSubstation(CoolingSubstation substation) {
        return substationRepository.save(substation);
    }
}
