package com.aafo.telemetry.seeder;

import com.aafo.telemetry.entity.Coordinates;
import com.aafo.telemetry.entity.DroneStatus;
import com.aafo.telemetry.entity.SensorData;
import com.aafo.telemetry.repository.DroneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class DroneDataSeeder implements ApplicationRunner {

    private final DroneRepository droneRepository;
    private final Random random = new Random(42L);

    private static final String[] SENSOR_TYPES = {
            "SONAR_PASSIVE", "SONAR_ACTIVE", "PRESSURE_TRANSDUCER",
            "TEMPERATURE_CTD", "OPTICAL_CAMERA"
    };

    private static final String[] STATUSES = {
            "ACTIVE", "IDLE", "RETURNING", "LOW_BATTERY", "FAULT"
    };

    @Override
    public void run(ApplicationArguments args) {
        log.info("DroneDataSeeder: seeding 20 drone documents …");
        List<DroneStatus> drones = new ArrayList<>();

        for (int i = 1; i <= 20; i++) {
            String droneId = String.format("drone-%02d", i);
            double depth   = 200.0 + random.nextDouble() * 3800.0; // 200–4000 m
            double battery = 10.0  + random.nextDouble() * 90.0;   // 10–100 %

            DroneStatus drone = DroneStatus.builder()
                    .droneId(droneId)
                    .batteryLevel(Math.round(battery * 100.0) / 100.0)
                    .depthMeters(Math.round(depth * 100.0) / 100.0)
                    .status(STATUSES[i % STATUSES.length])
                    .coordinates(randomCoordinates(depth))
                    .sensorData(buildSensorData(droneId))
                    .build();

            drones.add(drone);
        }

        droneRepository.saveAll(drones);
        log.info("DroneDataSeeder: inserted {} drone documents.", drones.size());
    }

    private Coordinates randomCoordinates(double depth) {
        return Coordinates.builder()
                .latitude(-89.0 + random.nextDouble() * 178.0)
                .longitude(-179.0 + random.nextDouble() * 358.0)
                .elevation(-depth)
                .build();
    }

    private List<SensorData> buildSensorData(String droneId) {
        List<SensorData> sensors = new ArrayList<>();
        for (int s = 0; s < 5; s++) {
            byte[] sonarBytes = new byte[38_400];
            random.nextBytes(sonarBytes);
            String b64Payload = Base64.getEncoder().encodeToString(sonarBytes);

            sensors.add(SensorData.builder()
                    .sensorType(SENSOR_TYPES[s])
                    .lastCalibrated(Instant.now().toEpochMilli() - random.nextLong(86_400_000L))
                    .rawPayload(b64Payload)
                    .build());
        }
        return sensors;
    }
}
