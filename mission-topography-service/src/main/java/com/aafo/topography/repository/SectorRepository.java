package com.aafo.topography.repository;

import com.aafo.topography.entity.Sector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectorRepository extends JpaRepository<Sector, String> {
    List<Sector> findAllById(Iterable<String> sectorIds);
}
