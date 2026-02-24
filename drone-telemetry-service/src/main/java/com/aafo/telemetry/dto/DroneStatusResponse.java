package com.aafo.telemetry.dto;

import com.aafo.telemetry.entity.Coordinates;
import com.aafo.telemetry.entity.SensorData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DroneStatusResponse {
    private String droneId;
    private Double batteryLevel;
    private Double depthMeters;
    private String status;
    private Coordinates coordinates;
    private List<SensorData> sensorData;
    private Double signalDegradation;
}
