package com.aafo.gateway.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProtocolContext {

    private Protocol protocol;

    private String clientType;

    public boolean isGrpc() {
        return Protocol.GRPC.equals(protocol);
    }

    public boolean isRest() {
        return Protocol.REST.equals(protocol);
    }
}
