package com.aafo.gateway.config;

import com.aafo.gateway.context.Protocol;
import com.aafo.gateway.context.ProtocolContext;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Intercepts all incoming GraphQL requests to extract the transport protocol 
 * and client type from the URI, injecting them into the GraphQLContext 
 * so DataLoaders know how to route outbound requests.
 */
@Component
public class ProtocolContextInterceptor implements WebGraphQlInterceptor {
    
    @Override
    public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
        String path = request.getUri().getPath();
        Protocol protocol = "grpc".equalsIgnoreCase(Optional.ofNullable(request.getHeaders().get("protocol")).orElse(List.of("rest")).getFirst()) ? Protocol.GRPC : Protocol.REST;
        String clientType = "WEB";
        
        ProtocolContext pc = ProtocolContext.builder()
                .protocol(protocol)
                .clientType(clientType)
                .build();
                
        request.configureExecutionInput((executionInput, builder) -> 
            builder.graphQLContext(Map.of("protocolContext", pc)).build()
        );
        
        return chain.next(request);
    }
}
