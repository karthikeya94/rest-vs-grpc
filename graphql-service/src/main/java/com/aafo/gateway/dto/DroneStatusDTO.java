package com.aafo.gateway.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DroneStatusDTO {
    private String droneId;
    private Double batteryLevel;
    private Double depthMeters;
    private String status;
    private CoordinatesDTO coordinates;
    private List<SensorDataDTO> sensorData;
    private Double signalDegradation;
}
