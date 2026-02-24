package com.aafo.topography.grpc;

import com.aafo.topography.entity.ThermalVent;
import com.aafo.topography.entity.WaterCurrent;
import com.aafo.topography.grpc.proto.*;
import com.aafo.topography.service.TopographyService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

import java.util.List;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class TopographyGrpcService extends TopographyServiceGrpc.TopographyServiceImplBase {

    private final TopographyService topographyService;

    @Override
    public void getSectorData(SectorRequest request,
                              StreamObserver<GetSectorResponse> responseObserver) {
        log.info("gRPC GetSectorData sectorId={}", request.getSectorId());
        try {
            com.aafo.topography.dto.SectorResponse dto =
                    topographyService.getSectorData(request.getSectorId());

            GetSectorResponse response = GetSectorResponse.newBuilder()
                    .setSector(toProto(dto))
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(
                    io.grpc.Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getBatchSectorData(BatchSectorRequest request,
                                   StreamObserver<GetBatchSectorResponse> responseObserver) {
        log.info("gRPC GetBatchSectorData {} sectors", request.getSectorIdsCount());

        List<com.aafo.topography.dto.SectorResponse> dtos =
                topographyService.getBatchSectorData(request.getSectorIdsList());

        GetBatchSectorResponse.Builder builder = GetBatchSectorResponse.newBuilder();
        dtos.forEach(dto -> builder.addSectors(toProto(dto)));

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    private SectorMessage toProto(com.aafo.topography.dto.SectorResponse dto) {
        SectorMessage.Builder b = SectorMessage.newBuilder()
                .setSectorId(s(dto.getSectorId()))
                .setMappingResolution(s(dto.getMappingResolution()))
                .setMinDepth(d(dto.getMinDepth()))
                .setMaxDepth(d(dto.getMaxDepth()));

        if (dto.getTopographyGrid() != null) {
            dto.getTopographyGrid().forEach(b::addTopographyGrid);
        }
        if (dto.getThermalVents() != null) {
            for (ThermalVent v : dto.getThermalVents()) {
                var vb = com.aafo.topography.grpc.proto.ThermalVent.newBuilder()
                        .setVentId(s(v.getVentId()))
                        .setTemperatureCelsius(d(v.getTemperatureCelsius()));
                if (v.getCoordinates() != null) {
                    vb.setCoordinates(TopoCoordinates.newBuilder()
                            .setLatitude(d(v.getCoordinates().getLatitude()))
                            .setLongitude(d(v.getCoordinates().getLongitude()))
                            .setElevation(d(v.getCoordinates().getElevation()))
                            .build());
                }
                b.addThermalVents(vb.build());
            }
        }
        if (dto.getWaterCurrents() != null) {
            for (WaterCurrent c : dto.getWaterCurrents()) {
                b.addWaterCurrents(com.aafo.topography.grpc.proto.WaterCurrent.newBuilder()
                        .setVectorId(s(c.getVectorId()))
                        .setDirectionDegrees(c.getDirectionDegrees() != null ? c.getDirectionDegrees() : 0)
                        .setVelocityKnots(d(c.getVelocityKnots()))
                        .build());
            }
        }
        return b.build();
    }

    private static String s(String v) { return v == null ? "" : v; }
    private static double d(Double v) { return v == null ? 0.0 : v; }
}
