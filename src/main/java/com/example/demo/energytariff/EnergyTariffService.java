package com.example.demo.energytariff;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EnergyTariffService {

    private final TariffRateRepository tariffRepository;

    @Autowired
    public EnergyTariffService(TariffRateRepository tariffRepository) {
        this.tariffRepository = tariffRepository;
    }

    public List<TariffRate> getAllTariffs() {
        return tariffRepository.findAll();
    }

    public Optional<TariffRate> getActiveTariffForCurrentTime() {
        int currentHour = LocalTime.now().getHour();
        return tariffRepository.findActiveTariffForHour(currentHour);
    }

    public Optional<TariffRate> getTariffForHour(int hour) {
        return tariffRepository.findActiveTariffForHour(hour);
    }

    public TariffRate saveTariff(TariffRate tariffRate) {
        return tariffRepository.save(tariffRate);
    }
}
