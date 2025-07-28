package com.dliriotech.tms.fleetservice.repository;

import com.dliriotech.tms.fleetservice.entity.TipoEquipo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface TipoEquipoRepository extends ReactiveCrudRepository<TipoEquipo, Integer> {
}