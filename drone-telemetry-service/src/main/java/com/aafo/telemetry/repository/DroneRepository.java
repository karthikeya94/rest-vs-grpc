package com.aafo.telemetry.repository;

import com.aafo.telemetry.entity.DroneStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DroneRepository extends JpaRepository<DroneStatus, String> {

    Optional<DroneStatus> findByDroneId(String droneId);

    @Query("SELECT d FROM DroneStatus d WHERE d.droneId IN :droneIds")
    List<DroneStatus> findAllByDroneIds(List<String> droneIds);
}
