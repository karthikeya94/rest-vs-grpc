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
public class ThermalVent implements Serializable {

    private String ventId;

    private Coordinates coordinates;

    private Double temperatureCelsius;
}
