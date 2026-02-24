package com.aafo.telemetry.grpc;

import com.aafo.telemetry.dto.DroneStatusResponse;
import com.aafo.telemetry.entity.Coordinates;
import com.aafo.telemetry.entity.SensorData;
import com.aafo.telemetry.grpc.proto.*;
import com.aafo.telemetry.service.TelemetryService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@GrpcService
public class DroneTelemetryGrpcService extends DroneTelemetryServiceGrpc.DroneTelemetryServiceImplBase {

    private final TelemetryService telemetryService;

    @Override
    public void getTelemetry(TelemetryRequest request, StreamObserver<TelemetryResponse> responseObserver) {
        log.info("gRPC GetTelemetry droneId={}", request.getDroneId());
        try {
            DroneStatusResponse dto = telemetryService.getTelemetry(request.getDroneId());
            TelemetryResponse response = TelemetryResponse.newBuilder()
                    .setDrone(toProto(dto))
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(io.grpc.Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getBatchTelemetry(BatchTelemetryRequest request, StreamObserver<BatchTelemetryResponse> responseObserver) {
        log.info("gRPC GetBatchTelemetry {} drones", request.getDroneIdsCount());

        List<DroneStatusResponse> dtos = telemetryService.getBatchTelemetry(request.getDroneIdsList());

        BatchTelemetryResponse.Builder builder = BatchTelemetryResponse.newBuilder();
        dtos.forEach(dto -> builder.addDrones(toProto(dto)));

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }


    private DroneStatusMessage toProto(DroneStatusResponse dto) {
        DroneStatusMessage.Builder b = DroneStatusMessage.newBuilder()
                .setDroneId(nullSafe(dto.getDroneId()))
                .setBatteryLevel(nvl(dto.getBatteryLevel()))
                .setDepthMeters(nvl(dto.getDepthMeters()))
                .setStatus(nullSafe(dto.getStatus()))
                .setSignalDegradation(nvl(dto.getSignalDegradation()));

        if (dto.getCoordinates() != null) {
            Coordinates c = dto.getCoordinates();
            b.setCoordinates(
                    com.aafo.telemetry.grpc.proto.Coordinates.newBuilder()
                            .setLatitude(nvl(c.getLatitude()))
                            .setLongitude(nvl(c.getLongitude()))
                            .setElevation(nvl(c.getElevation()))
                            .build());
        }

        if (dto.getSensorData() != null) {
            for (SensorData sd : dto.getSensorData()) {
                b.addSensorData(
                        com.aafo.telemetry.grpc.proto.SensorData.newBuilder()
                                .setSensorType(nullSafe(sd.getSensorType()))
                                .setLastCalibrated(nvlLong(sd.getLastCalibrated()))
                                .setRawPayload(nullSafe(sd.getRawPayload()))
                                .build());
            }
        }
        return b.build();
    }

    private static String nullSafe(String s) {
        return s == null ? "" : s;
    }

    private static double nvl(Double d) {
        return d == null ? 0.0 : d;
    }

    private static long nvlLong(Long l) {
        return l == null ? 0L : l;
    }
}
