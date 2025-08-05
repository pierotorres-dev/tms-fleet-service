package com.dliriotech.tms.fleetservice.repository;

import com.dliriotech.tms.fleetservice.entity.TipoObservacion;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface TipoObservacionRepository extends ReactiveCrudRepository<TipoObservacion, Integer> {
    Flux<TipoObservacion> findAllByAmbito(String ambito);
}