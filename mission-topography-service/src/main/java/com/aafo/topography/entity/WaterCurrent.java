package com.aafo.topography.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaterCurrent implements Serializable {

    private String vectorId;

    private Integer directionDegrees;

    private Double velocityKnots;
}
