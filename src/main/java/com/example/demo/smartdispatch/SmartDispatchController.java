package com.example.demo.smartdispatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dispatch")
@CrossOrigin(origins = "*")
public class SmartDispatchController {

    private final SmartDispatchService dispatchService;

    @Autowired
    public SmartDispatchController(SmartDispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }

    @GetMapping("/plan")
    public ResponseEntity<Map<String, Object>> getOptimalDispatchPlan() {
        return ResponseEntity.ok(dispatchService.calculateOptimalDispatchPlan());
    }

    @PostMapping("/execute")
    public ResponseEntity<DispatchStrategy> executeDispatch() {
        return ResponseEntity.ok(dispatchService.executeAndRecordDispatch());
    }

    @GetMapping("/history")
    public ResponseEntity<List<DispatchStrategy>> getHistory() {
        return ResponseEntity.ok(dispatchService.getRecentDispatchHistory());
    }

    @GetMapping("/savings")
    public ResponseEntity<Map<String, Object>> getTotalSavings() {
        Map<String, Object> response = new HashMap<>();
        response.put("cumulativeSavingsUSD", dispatchService.getTotalCumulativeSavingsUSD());
        return ResponseEntity.ok(response);
    }
}
