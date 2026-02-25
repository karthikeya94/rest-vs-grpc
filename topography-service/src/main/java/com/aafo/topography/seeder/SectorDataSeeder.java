package com.aafo.topography.seeder;

import com.aafo.topography.entity.Coordinates;
import com.aafo.topography.entity.Sector;
import com.aafo.topography.entity.ThermalVent;
import com.aafo.topography.entity.WaterCurrent;
import com.aafo.topography.repository.SectorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class SectorDataSeeder implements ApplicationRunner {

    private final SectorRepository sectorRepository;
    private final Random random = new Random(7L); 

    private static final String[] RESOLUTIONS = {"1m", "2m", "5m", "10m"};

    @Override
    public void run(ApplicationArguments args) {
        if (sectorRepository.count() > 0) {
            log.info("SectorDataSeeder: collection already seeded ({} documents). Skipping.",
                    sectorRepository.count());
            return;
        }

        log.info("SectorDataSeeder: seeding 10 sector documents …");
        List<Sector> sectors = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            String sectorId = String.format("sector-%02d", i);

            sectors.add(Sector.builder()
                    .sectorId(sectorId)
                    .mappingResolution(RESOLUTIONS[i % RESOLUTIONS.length])
                    .topographyGrid(buildTopographyGrid())
                    .thermalVents(buildThermalVents(sectorId))
                    .waterCurrents(buildWaterCurrents(sectorId))
                    .build());
        }

        sectorRepository.saveAll(sectors);
        log.info("SectorDataSeeder: inserted {} sector documents.", sectors.size());
    }

    private List<Double> buildTopographyGrid() {
        List<Double> grid = new ArrayList<>(12_000);
        for (int i = 0; i < 12_000; i++) {
            double depth = -(200.0 + random.nextDouble() * 5800.0);
            grid.add(Math.round(depth * 100.0) / 100.0);
        }
        return grid;
    }

    private List<ThermalVent> buildThermalVents(String sectorId) {
        List<ThermalVent> vents = new ArrayList<>();
        for (int v = 1; v <= 5; v++) {
            vents.add(ThermalVent.builder()
                    .ventId(sectorId + "-vent-" + v)
                    .coordinates(Coordinates.builder()
                            .latitude(-89.0 + random.nextDouble() * 178.0)
                            .longitude(-179.0 + random.nextDouble() * 358.0)
                            .elevation(-(200.0 + random.nextDouble() * 3000.0))
                            .build())
                    .temperatureCelsius(60.0 + random.nextDouble() * 340.0)  // 60–400 °C
                    .build());
        }
        return vents;
    }

    private List<WaterCurrent> buildWaterCurrents(String sectorId) {
        List<WaterCurrent> currents = new ArrayList<>();
        for (int c = 1; c <= 8; c++) {
            currents.add(WaterCurrent.builder()
                    .vectorId(sectorId + "-current-" + c)
                    .directionDegrees(random.nextInt(360))
                    .velocityKnots(0.1 + random.nextDouble() * 4.9)  // 0.1–5.0 knots
                    .build());
        }
        return currents;
    }
}
