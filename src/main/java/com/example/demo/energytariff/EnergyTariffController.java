package com.example.demo.energytariff;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tariffs")
@CrossOrigin(origins = "*")
public class EnergyTariffController {

    private final EnergyTariffService tariffService;

    @Autowired
    public EnergyTariffController(EnergyTariffService tariffService) {
        this.tariffService = tariffService;
    }

    @GetMapping
    public ResponseEntity<List<TariffRate>> getAllTariffs() {
        return ResponseEntity.ok(tariffService.getAllTariffs());
    }

    @GetMapping("/current")
    public ResponseEntity<TariffRate> getCurrentTariff() {
        return tariffService.getActiveTariffForCurrentTime()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/hour/{hour}")
    public ResponseEntity<TariffRate> getTariffForHour(@PathVariable int hour) {
        return tariffService.getTariffForHour(hour)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TariffRate> saveTariff(@RequestBody TariffRate tariff) {
        return ResponseEntity.ok(tariffService.saveTariff(tariff));
    }
}
