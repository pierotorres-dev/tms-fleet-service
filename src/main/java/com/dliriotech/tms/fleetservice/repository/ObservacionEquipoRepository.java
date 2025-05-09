package com.dliriotech.tms.fleetservice.repository;

import com.dliriotech.tms.fleetservice.entity.ObservacionEquipo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ObservacionEquipoRepository extends ReactiveCrudRepository<ObservacionEquipo, Integer> {
    Flux<ObservacionEquipo> findByEquipoId(Integer equipoId);
}