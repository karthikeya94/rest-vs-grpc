package com.aafo.telemetry.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "drones")
public class DroneStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "drone_id")
    private String droneId;

    @Column(name = "battery_level")
    private Double batteryLevel;

    @Column(name = "depth_meters")
    private Double depthMeters;
    
    @Column(name = "status")
    private String status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "coordinates", columnDefinition = "jsonb")
    private Coordinates coordinates;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "sensor_data", columnDefinition = "jsonb")
    private List<SensorData> sensorData;

    @Transient
    private Double signalDegradation;
}
