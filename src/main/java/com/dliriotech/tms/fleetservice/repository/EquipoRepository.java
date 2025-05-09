package com.dliriotech.tms.fleetservice.repository;

import com.dliriotech.tms.fleetservice.entity.Equipo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface EquipoRepository extends ReactiveCrudRepository<Equipo, Integer> {
    Flux<Equipo> findByEmpresaId(Integer empresaId);
}