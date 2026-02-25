package com.aafo.topography.dto;

import com.aafo.topography.entity.ThermalVent;
import com.aafo.topography.entity.WaterCurrent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectorResponse {
    private String sectorId;
    private String mappingResolution;
    private List<Double> topographyGrid;
    private List<ThermalVent> thermalVents;
    private List<WaterCurrent> waterCurrents;
    private Double minDepth;
    private Double maxDepth;
}
