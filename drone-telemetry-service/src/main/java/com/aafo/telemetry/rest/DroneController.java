package com.aafo.telemetry.rest;

import com.aafo.telemetry.dto.DroneStatusResponse;
import com.aafo.telemetry.service.TelemetryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/telemetry")
@RequiredArgsConstructor
public class DroneController {

    private final TelemetryService telemetryService;

    @GetMapping("/{droneId}")
    public ResponseEntity<DroneStatusResponse> getTelemetry(@PathVariable String droneId) {
        log.info("REST GetTelemetry request for droneId={}", droneId);
        try {
            DroneStatusResponse response = telemetryService.getTelemetry(droneId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/batch")
    public ResponseEntity<List<DroneStatusResponse>> getBatchTelemetry(@RequestBody List<String> droneIds) {
        log.info("REST GetBatchTelemetry request for {} drones", droneIds.size());
        List<DroneStatusResponse> responses = telemetryService.getBatchTelemetry(droneIds);
        return ResponseEntity.ok(responses);
    }
}
