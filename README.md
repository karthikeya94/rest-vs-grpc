## Deep-Sea Autonomous Fleet Orchestrator


An end-to-end microservices architecture designed to benchmark and demonstrate the performance delta between **gRPC** and **REST (HTTP/1.1)** for internal service-to-service communication, orchestrated behind a dynamic **GraphQL Gateway**.

This project utilizes **Java 21 Virtual Threads** to completely eliminate traditional thread-pool contention and complex asynchronous code (`CompletableFuture`), isolating the benchmark entirely to network and protocol efficiency.

## Key Features & Architectural Patterns
* **Dynamic Protocol Routing:** The GraphQL Gateway dynamically resolves massive nested datasets by routing queries to downstream microservices via either Spring RestClient or a gRPC Stub based on the endpoint invoked.
* **Java 21 Virtual Threads:** Fully enabled across all services (`spring.threads.virtual.enabled=true`). Thousands of concurrent requests are handled using simple, blocking code without overwhelming the JVM.
* **Programmatic GraphQL:** Strictly avoids annotation-based frameworks (like DGS or `@SchemaMapping`) in favor of pure `graphql-java` `RuntimeWiring` and `DataLoaders` to optimize N+1 queries.
* **Heavy Payload Simulation:** The domain involves dense 3D bathymetric matrices (`TopographyGrid`) and array-heavy `SensorData` to aggressively test the JSON vs. Protobuf serialization ceilings.

## System Architecture

The domain simulates mapping the ocean floor using a swarm of autonomous drones.

1.  **GraphQL Gateway (Port 8080):** The orchestration node. Exposes `/graphql/web/rest` and `/graphql/web/grpc` endpoints.
2.  **Telemetry Service (Port 8081 / gRPC 9091):** Manages high-frequency drone status, coordinates, and raw sensor payloads. Backed by MongoDB.
3.  **Topography Service (Port 8082 / gRPC 9092):** Serves massive 3D mapping matrices and thermal vent data. Backed by MongoDB.

## Prerequisites
* **Java 21** & **Maven**
* **MongoDB** (Running locally on default port `27017`)
* **Podman** or **Docker** (For running the k6 load tests)
* **Grafana k6** (`podman pull grafana/k6`)

## Quick Start

**1. Clone and Build**
```bash
git clone [https://github.com/yourusername/graphql-grpc-rest-benchmark.git](https://github.com/yourusername/graphql-grpc-rest-benchmark.git)
cd graphql-grpc-rest-benchmark
mvn clean install
