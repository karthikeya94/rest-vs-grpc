package com.aafo.topography.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sectors")
public class Sector {

    @Id
    @Column(name = "sector_id")
    private String sectorId;

    @Column(name = "mapping_resolution")
    private String mappingResolution;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "topography_grid", columnDefinition = "jsonb")
    private List<Double> topographyGrid;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "thermal_vents", columnDefinition = "jsonb")
    private List<ThermalVent> thermalVents;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "water_currents", columnDefinition = "jsonb")
    private List<WaterCurrent> waterCurrents;

    @Transient
    private Double minDepth;

    @Transient
    private Double maxDepth;
}
