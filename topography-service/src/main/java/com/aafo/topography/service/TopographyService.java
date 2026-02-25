package com.aafo.topography.service;

import com.aafo.topography.dto.SectorResponse;
import com.aafo.topography.entity.Sector;
import com.aafo.topography.repository.SectorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopographyService {

    private final SectorRepository sectorRepository;

    public SectorResponse getSectorData(String sectorId) {
        Sector sector = sectorRepository.findById(sectorId)
                .orElseThrow(() -> new IllegalArgumentException("Sector not found: " + sectorId));

        log.info("GetSectorData sectorId={} gridSize={}", sectorId,
                sector.getTopographyGrid() != null ? sector.getTopographyGrid().size() : 0);

        return toResponse(sector);
    }

    public List<SectorResponse> getBatchSectorData(List<String> sectorIds) {
        log.info("GetBatchSectorData requested {} sectors", sectorIds.size());

        List<Sector> found = sectorRepository.findAllById(sectorIds);
        Map<String, Sector> byId = found.stream()
                .collect(Collectors.toMap(Sector::getSectorId, Function.identity()));

        return sectorIds.stream()
                .map(id -> {
                    Sector s = byId.get(id);
                    if (s == null) {
                        log.warn("Sector {} not found in batch, returning placeholder", id);
                        return SectorResponse.builder().sectorId(id).build();
                    }
                    return toResponse(s);
                })
                .collect(Collectors.toList());
    }


    public SectorResponse toResponse(Sector sector) {
        double minDepth = 0.0, maxDepth = 0.0;

        if (sector.getTopographyGrid() != null && !sector.getTopographyGrid().isEmpty()) {
            DoubleSummaryStatistics stats = sector.getTopographyGrid().stream()
                    .mapToDouble(Double::doubleValue)
                    .summaryStatistics();
            minDepth = stats.getMin();
            maxDepth = stats.getMax();
        }

        return SectorResponse.builder()
                .sectorId(sector.getSectorId())
                .mappingResolution(sector.getMappingResolution())
                .topographyGrid(sector.getTopographyGrid())
                .thermalVents(sector.getThermalVents())
                .waterCurrents(sector.getWaterCurrents())
                .minDepth(minDepth)
                .maxDepth(maxDepth)
                .build();
    }
}
