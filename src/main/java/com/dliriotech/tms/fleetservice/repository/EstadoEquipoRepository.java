package com.dliriotech.tms.fleetservice.repository;

import com.dliriotech.tms.fleetservice.entity.EstadoEquipo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface EstadoEquipoRepository extends ReactiveCrudRepository<EstadoEquipo, Integer> {
}
