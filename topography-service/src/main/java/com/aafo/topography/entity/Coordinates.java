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
public class Coordinates implements Serializable {
    private Double latitude;
    private Double longitude;
    private Double elevation;
}
