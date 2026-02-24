package com.aafo.telemetry.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensorData implements Serializable {

    private String sensorType;

    private Long lastCalibrated;

    private String rawPayload;
}
