package com.aafo.telemetry.service;

import com.aafo.telemetry.dto.DroneStatusResponse;
import com.aafo.telemetry.entity.DroneStatus;
import com.aafo.telemetry.repository.DroneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelemetryService {

    private final DroneRepository droneRepository;

    public DroneStatusResponse getTelemetry(String droneId) {
        DroneStatus drone = droneRepository.findByDroneId(droneId)
                .orElseThrow(() -> new IllegalArgumentException("Drone not found: " + droneId));

        double degradation = computeSignalDegradation(drone.getDepthMeters());
        log.info("GetTelemetry droneId={} depth={}m signalDegradation={}%",
                droneId, drone.getDepthMeters(), degradation);

        return toResponse(drone, degradation);
    }

    public List<DroneStatusResponse> getBatchTelemetry(List<String> droneIds) {
        log.info("GetBatchTelemetry requested {} drones", droneIds.size());

        List<DroneStatus> found = droneRepository.findAllByDroneIds(droneIds);

        Map<String, DroneStatus> byId = found.stream()
                .collect(Collectors.toMap(DroneStatus::getDroneId, Function.identity()));

        return droneIds.stream()
                .map(id -> {
                    DroneStatus ds = byId.get(id);
                    if (ds == null) {
                        log.warn("Drone {} not found in batch, returning placeholder", id);
                        return DroneStatusResponse.builder().droneId(id).status("NOT_FOUND").build();
                    }
                    return toResponse(ds, computeSignalDegradation(ds.getDepthMeters()));
                }).toList();
    }

    public double computeSignalDegradation(Double depthMeters) {
        if (depthMeters == null || depthMeters <= 0) return 0.0;
        double raw = (depthMeters / 100.0) * 3.0;
        return Math.min(raw, 100.0);
    }

    private DroneStatusResponse toResponse(DroneStatus ds, double degradation) {
        return DroneStatusResponse.builder()
                .droneId(ds.getDroneId())
                .batteryLevel(ds.getBatteryLevel())
                .depthMeters(ds.getDepthMeters())
                .status(ds.getStatus())
                .coordinates(ds.getCoordinates())
                .sensorData(ds.getSensorData())
                .signalDegradation(degradation)
                .build();
    }
}
