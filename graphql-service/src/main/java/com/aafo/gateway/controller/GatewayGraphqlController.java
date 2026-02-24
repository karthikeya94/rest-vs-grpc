package com.aafo.gateway.controller;

import com.aafo.gateway.client.GrpcDownstreamClient;
import com.aafo.gateway.client.RestDownstreamClient;
import com.aafo.gateway.context.ProtocolContext;
import com.aafo.gateway.dto.DroneStatusDTO;
import com.aafo.gateway.dto.SectorDataDTO;
import com.aafo.gateway.fetcher.RiskScoreCalculator;
import graphql.GraphQLContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GatewayGraphqlController {

    private final RestDownstreamClient restClient;
    private final GrpcDownstreamClient grpcClient;
    private final RiskScoreCalculator riskScoreCalculator;

    @QueryMapping
    public DroneStatusDTO drone(@Argument String droneId, GraphQLContext context) {
        return fetchBatchDrones(List.of(droneId), context).stream().findFirst().orElse(null);
    }

    @QueryMapping
    public List<DroneStatusDTO> drones(@Argument List<String> droneIds, GraphQLContext context) {
        return fetchBatchDrones(droneIds, context);
    }

    @QueryMapping
    public SectorDataDTO sector(@Argument String sectorId, GraphQLContext context) {
        return fetchBatchSectors(List.of(sectorId), context).stream().findFirst().orElse(null);
    }

    @QueryMapping
    public List<SectorDataDTO> sectors(@Argument List<String> sectorIds, GraphQLContext context) {
        return fetchBatchSectors(sectorIds, context);
    }

    @BatchMapping(typeName = "Drone", field = "sector")
    public Map<DroneStatusDTO, SectorDataDTO> sector(List<DroneStatusDTO> drones, GraphQLContext context) {
        log.info("@BatchMapping resolving 'sector' for {} drones", drones.size());
        
        List<String> sectorIds = drones.stream()
                .map(this::getSectorIdForDrone)
                .distinct()
                .collect(Collectors.toList());

        List<SectorDataDTO> sectors = fetchBatchSectors(sectorIds, context);
        Map<String, SectorDataDTO> sectorMap = sectors.stream()
                .collect(Collectors.toMap(SectorDataDTO::getSectorId, s -> s));

        return drones.stream().collect(Collectors.toMap(
                d -> d,
                d -> sectorMap.get(getSectorIdForDrone(d))
        ));
    }

    @BatchMapping(typeName = "Drone", field = "riskScore")
    public Map<DroneStatusDTO, Map<String, Object>> riskScore(List<DroneStatusDTO> drones, GraphQLContext context) {
        log.info("@BatchMapping resolving 'riskScore' for {} drones", drones.size());
        
        List<String> sectorIds = drones.stream()
                .map(this::getSectorIdForDrone)
                .distinct()
                .collect(Collectors.toList());

        List<SectorDataDTO> sectors = fetchBatchSectors(sectorIds, context);
        Map<String, SectorDataDTO> sectorMap = sectors.stream()
                .collect(Collectors.toMap(SectorDataDTO::getSectorId, s -> s));

        return drones.stream().collect(Collectors.toMap(
                d -> d,
                d -> {
                    String sId = getSectorIdForDrone(d);
                    return riskScoreCalculator.computeRiskMap(d, sId, sectorMap.get(sId));
                }
        ));
    }

    private List<DroneStatusDTO> fetchBatchDrones(List<String> droneIds, GraphQLContext context) {
        System.out.println(context.toString());
        ProtocolContext pc = context.get("protocolContext");
        if (pc != null && pc.isGrpc()) {
            return grpcClient.fetchBatchDrones(droneIds);
        }
        return restClient.fetchBatchDrones(droneIds);
    }

    private List<SectorDataDTO> fetchBatchSectors(List<String> sectorIds, GraphQLContext context) {
        ProtocolContext pc = context.get("protocolContext");
        if (pc != null && pc.isGrpc()) {
            return grpcClient.fetchBatchSectors(sectorIds);
        }
        return restClient.fetchBatchSectors(sectorIds);
    }

    private String getSectorIdForDrone(DroneStatusDTO drone) {
        return "sector-" + String.format("%02d", (Math.abs(drone.getDroneId().hashCode()) % 10) + 1);
    }
}
