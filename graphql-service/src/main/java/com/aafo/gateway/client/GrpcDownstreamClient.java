package com.aafo.gateway.client;

import com.aafo.gateway.dto.*;
import com.aafo.telemetry.grpc.proto.*;
import com.aafo.topography.grpc.proto.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class GrpcDownstreamClient {

    private final String telemetryHost;
    private final int    telemetryPort;
    private final String topographyHost;
    private final int    topographyPort;

    private ManagedChannel telemetryChannel;
    private ManagedChannel topographyChannel;
    private DroneTelemetryServiceGrpc.DroneTelemetryServiceBlockingStub telemetryStub;
    private TopographyServiceGrpc.TopographyServiceBlockingStub topographyStub;

    public GrpcDownstreamClient(
            @Value("${downstream.telemetry.grpc-host}") String telemetryHost,
            @Value("${downstream.telemetry.grpc-port}") int telemetryPort,
            @Value("${downstream.topography.grpc-host}") String topographyHost,
            @Value("${downstream.topography.grpc-port}") int topographyPort) {
        this.telemetryHost  = telemetryHost;
        this.telemetryPort  = telemetryPort;
        this.topographyHost = topographyHost;
        this.topographyPort = topographyPort;
    }

    @PostConstruct
    public void init() {
        telemetryChannel = ManagedChannelBuilder
                .forAddress(telemetryHost, telemetryPort)
                .usePlaintext()
                .build();
        topographyChannel = ManagedChannelBuilder
                .forAddress(topographyHost, topographyPort)
                .usePlaintext()
                .build();

        telemetryStub  = DroneTelemetryServiceGrpc.newBlockingStub(telemetryChannel);
        topographyStub = TopographyServiceGrpc.newBlockingStub(topographyChannel);
        log.info("gRPC channels initialized — telemetry={}:{} topography={}:{}",
                telemetryHost, telemetryPort, topographyHost, topographyPort);
    }

    @PreDestroy
    public void shutdown() {
        telemetryChannel.shutdown();
        topographyChannel.shutdown();
    }

    public DroneStatusDTO fetchDrone(String droneId) {
        log.debug("gRPC → GetTelemetry droneId={}", droneId);
        TelemetryResponse resp = telemetryStub.getTelemetry(
                TelemetryRequest.newBuilder().setDroneId(droneId).build());
        return mapDrone(resp.getDrone());
    }

    public List<DroneStatusDTO> fetchBatchDrones(List<String> droneIds) {
        log.debug("gRPC → GetBatchTelemetry count={}", droneIds.size());
        BatchTelemetryResponse resp = telemetryStub.getBatchTelemetry(
                BatchTelemetryRequest.newBuilder().addAllDroneIds(droneIds).build());
        return  resp.getDronesList().stream()
                .map(this::mapDrone)
                .collect(Collectors.toList());
    }

    public SectorDataDTO fetchSector(String sectorId) {
        log.debug("gRPC → GetSectorData sectorId={}", sectorId);
        GetSectorResponse resp = topographyStub.getSectorData(
                SectorRequest.newBuilder().setSectorId(sectorId).build());
        return mapSector(resp.getSector());
    }

    public List<SectorDataDTO> fetchBatchSectors(List<String> sectorIds) {
        log.debug("gRPC → GetBatchSectorData count={}", sectorIds.size());
        GetBatchSectorResponse resp = topographyStub.getBatchSectorData(
                BatchSectorRequest.newBuilder().addAllSectorIds(sectorIds).build());
        return resp.getSectorsList().stream()
                .map(this::mapSector)
                .collect(Collectors.toList());
    }

    private DroneStatusDTO mapDrone(DroneStatusMessage m) {
        CoordinatesDTO coords = null;
        if (m.hasCoordinates()) {
            var c = m.getCoordinates();
            coords = new CoordinatesDTO(c.getLatitude(), c.getLongitude(), c.getElevation());
        }
        List<SensorDataDTO> sensors = m.getSensorDataList().stream()
                .map(s -> new SensorDataDTO(s.getSensorType(), s.getLastCalibrated(), s.getRawPayload()))
                .collect(Collectors.toList());

        return DroneStatusDTO.builder()
                .droneId(m.getDroneId())
                .batteryLevel(m.getBatteryLevel())
                .depthMeters(m.getDepthMeters())
                .status(m.getStatus())
                .coordinates(coords)
                .sensorData(sensors)
                .signalDegradation(m.getSignalDegradation())
                .build();
    }

    private SectorDataDTO mapSector(SectorMessage m) {
        List<ThermalVentDTO> vents = m.getThermalVentsList().stream()
                .map(v -> {
                    CoordinatesDTO vc = v.hasCoordinates()
                            ? new CoordinatesDTO(v.getCoordinates().getLatitude(),
                                                 v.getCoordinates().getLongitude(),
                                                 v.getCoordinates().getElevation())
                            : null;
                    return new ThermalVentDTO(v.getVentId(), vc, v.getTemperatureCelsius());
                })
                .collect(Collectors.toList());

        List<WaterCurrentDTO> currents = m.getWaterCurrentsList().stream()
                .map(c -> new WaterCurrentDTO(c.getVectorId(), c.getDirectionDegrees(), c.getVelocityKnots()))
                .collect(Collectors.toList());

        return SectorDataDTO.builder()
                .sectorId(m.getSectorId())
                .mappingResolution(m.getMappingResolution())
                .topographyGrid(m.getTopographyGridList())
                .thermalVents(vents)
                .waterCurrents(currents)
                .minDepth(m.getMinDepth())
                .maxDepth(m.getMaxDepth())
                .build();
    }
}
