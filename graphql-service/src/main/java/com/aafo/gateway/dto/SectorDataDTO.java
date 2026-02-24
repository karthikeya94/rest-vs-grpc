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
public class SectorDataDTO {
    private String sectorId;
    private String mappingResolution;
    private List<Double> topographyGrid;
    private List<ThermalVentDTO> thermalVents;
    private List<WaterCurrentDTO> waterCurrents;
    private Double minDepth;
    private Double maxDepth;
}
