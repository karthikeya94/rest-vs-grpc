package com.aafo.topography.rest;

import com.aafo.topography.dto.SectorResponse;
import com.aafo.topography.service.TopographyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/topography")
@RequiredArgsConstructor
public class SectorController {

    private final TopographyService topographyService;

    @GetMapping("/{sectorId}")
    public ResponseEntity<SectorResponse> getSectorData(@PathVariable String sectorId) {
        log.info("REST GetSectorData sectorId={}", sectorId);
        try {
            return ResponseEntity.ok(topographyService.getSectorData(sectorId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/batch")
    public ResponseEntity<List<SectorResponse>> getBatchSectorData(
            @RequestBody List<String> sectorIds) {
        log.info("REST GetBatchSectorData {} sectors", sectorIds.size());
        return ResponseEntity.ok(topographyService.getBatchSectorData(sectorIds));
    }
}
