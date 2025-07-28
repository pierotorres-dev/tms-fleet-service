package com.dliriotech.tms.fleetservice.repository;

import com.dliriotech.tms.fleetservice.entity.EsquemaEquipo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface EsquemaEquipoRepository extends ReactiveCrudRepository<EsquemaEquipo, Integer> {
    Flux<EsquemaEquipo> findByTipoEquipoId(Integer tipoEquipoId);
}