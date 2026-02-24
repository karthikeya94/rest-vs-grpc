package com.aafo.gateway.fetcher;

import com.aafo.gateway.dto.DroneStatusDTO;
import com.aafo.gateway.dto.SectorDataDTO;
import com.aafo.gateway.dto.ThermalVentDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class RiskScoreCalculator {

    public Map<String, Object> computeRiskMap(DroneStatusDTO drone, String sectorId, SectorDataDTO sector) {
        if (sector == null) {
            return Map.of("score", 0.0, "riskLevel", "UNKNOWN");
        }

        long startTime = System.currentTimeMillis();
        
        double droneDepth = drone.getDepthMeters() != null ? drone.getDepthMeters() : 0;
        double battery = drone.getBatteryLevel() != null ? drone.getBatteryLevel() : 100;

        double riskScore = 0.0;
        
        List<Double> grid = sector.getTopographyGrid();
        if (grid != null) {
            int dangerPoints = 0;
            for (Double seafloorDepth : grid) {
                if (Math.abs(droneDepth - Math.abs(seafloorDepth)) < 10) {
                    dangerPoints++;
                }
            }
            riskScore += Math.min((dangerPoints / (double) grid.size()) * 100 * 5, 40.0);
        }

        if (sector.getThermalVents() != null) {
            for (ThermalVentDTO vent : sector.getThermalVents()) {
                if (vent.getTemperatureCelsius() > 100) {
                    riskScore += 5.0; 
                }
            }
        }

        if (battery < 20.0) {
            riskScore += 30.0;
        }

        riskScore = Math.min(Math.max(riskScore, 0.0), 100.0);
        String riskLevel = riskScore > 80 ? "CRITICAL" : (riskScore > 50 ? "WARNING" : "SAFE");

        long timeTaken = System.currentTimeMillis() - startTime;

        return Map.of(
                "droneId", drone.getDroneId(),
                "sectorId", sectorId,
                "score", riskScore,
                "riskLevel", riskLevel,
                "computationTimeMs", timeTaken,
                "factors", Map.of(
                        "depthPressureIndex", droneDepth / 100.0,
                        "batteryRiskFactor", 100.0 - battery
                )
        );
    }
}
