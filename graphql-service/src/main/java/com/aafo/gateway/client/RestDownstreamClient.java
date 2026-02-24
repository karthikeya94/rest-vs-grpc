package com.aafo.gateway.client;

import com.aafo.gateway.dto.DroneStatusDTO;
import com.aafo.gateway.dto.SectorDataDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Component
public class RestDownstreamClient {

    private final RestClient telemetryClient;
    private final RestClient topographyClient;

    public RestDownstreamClient(
            @Value("${downstream.telemetry.base-url}") String telemetryBaseUrl,
            @Value("${downstream.topography.base-url}") String topographyBaseUrl) {
        this.telemetryClient  = RestClient.builder().baseUrl(telemetryBaseUrl).build();
        this.topographyClient = RestClient.builder().baseUrl(topographyBaseUrl).build();
    }

    public DroneStatusDTO fetchDrone(String droneId) {
        log.debug("REST → GET /telemetry/{}", droneId);
        return telemetryClient.get()
                .uri("/telemetry/{id}", droneId)
                .retrieve()
                .body(DroneStatusDTO.class);
    }

    public List<DroneStatusDTO> fetchBatchDrones(List<String> droneIds) {
        log.debug("REST → POST /telemetry/batch (count={})", droneIds.size());
        return telemetryClient.post()
                .uri("/telemetry/batch")
                .body(droneIds)
                .retrieve()
                .body(new ParameterizedTypeReference<List<DroneStatusDTO>>() {});
    }

    public SectorDataDTO fetchSector(String sectorId) {
        log.debug("REST → GET /topography/{}", sectorId);
        return topographyClient.get()
                .uri("/topography/{id}", sectorId)
                .retrieve()
                .body(SectorDataDTO.class);
    }

    public List<SectorDataDTO> fetchBatchSectors(List<String> sectorIds) {
        log.debug("REST → POST /topography/batch (count={})", sectorIds.size());
        return topographyClient.post()
                .uri("/topography/batch")
                .body(sectorIds)
                .retrieve()
                .body(new ParameterizedTypeReference<List<SectorDataDTO>>() {});
    }
}
