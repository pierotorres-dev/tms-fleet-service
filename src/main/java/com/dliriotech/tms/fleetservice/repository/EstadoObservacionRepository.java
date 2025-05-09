package com.dliriotech.tms.fleetservice.repository;

import com.dliriotech.tms.fleetservice.entity.EstadoObservacion;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface EstadoObservacionRepository extends ReactiveCrudRepository<EstadoObservacion, Integer> {
}
