package com.risk.management.controller;

import com.risk.management.model.Risk;
import com.risk.management.model.RiskCategory;
import com.risk.management.model.RiskStatus;
import com.risk.management.service.RiskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/risks")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class RiskController {

    private final RiskService riskService;

    public RiskController(RiskService riskService) {
        this.riskService = riskService;
    }

    @GetMapping
    public ResponseEntity<List<Risk>> getAllRisks(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status) {

        if (category != null) {
            return ResponseEntity.ok(riskService.getRisksByCategory(RiskCategory.valueOf(category)));
        }
        if (status != null) {
            return ResponseEntity.ok(riskService.getRisksByStatus(RiskStatus.valueOf(status)));
        }
        return ResponseEntity.ok(riskService.getAllRisks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Risk> getRisk(@PathVariable Long id) {
        return riskService.getRiskById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Risk> createRisk(@Valid @RequestBody Risk risk) {
        return ResponseEntity.status(HttpStatus.CREATED).body(riskService.createRisk(risk));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Risk> updateRisk(@PathVariable Long id, @Valid @RequestBody Risk risk) {
        try {
            return ResponseEntity.ok(riskService.updateRisk(id, risk));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRisk(@PathVariable Long id) {
        try {
            riskService.deleteRisk(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        return ResponseEntity.ok(riskService.getDashboardStats());
    }

    @GetMapping("/enums")
    public ResponseEntity<Map<String, Object>> getEnums() {
        return ResponseEntity.ok(Map.of(
                "categories", RiskCategory.values(),
                "statuses", RiskStatus.values()
        ));
    }
}
